import React, { useState } from "react";
import { Editor } from "react-draft-wysiwyg";
import { EditorState, convertToRaw, ContentBlock } from "draft-js";
import draftToHtml from "draftjs-to-html";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import DOMPurify from "dompurify";

import PostRequestWithAccessToken from "../api/axios/requestWithAccessToken";
import { useDispatch, useSelector } from "react-redux";
import { popModal, showModal } from "../redux/modalStackSlice";

// 이미지 렌더링용 컴포넌트
function MediaComponent({ block, contentState }) {
  const entityKey = block.getEntityAt(0);
  if (!entityKey) return null;

  const data = contentState.getEntity(entityKey).getData();

  return (
    <div>
      <img
        src={data.src}
        alt={data.alt || ""}
        style={{
          height: data.height || "auto",
          width: data.width || "100%",
          maxWidth: "100%",
        }}
      />
    </div>
  );
}

// atomic 블록 감지 시 MediaComponent로 렌더링
function imageBlockRenderer(contentBlock) {
  if (contentBlock.getType() === "atomic") {
    return {
      component: MediaComponent,
      editable: false,
    };
  }
  return null;
}

const PostTemp = ({ z }) => {
  const [editorState, setEditorState] = useState(EditorState.createEmpty());
  const [imageFiles, setImageFiles] = useState([]);

  const token = useSelector((state) => state.auth.accessToken);

  const dispatch = useDispatch();

  const onEditorStateChange = (state) => {
    setEditorState(state);
  };

  const uploadImageCallback = (file) => {
    const localUrl = URL.createObjectURL(file);
    setImageFiles((prev) => [...prev, { file, localUrl }]);

    return Promise.resolve({ data: { link: localUrl } });
  };

  const uploadImageToServer = async (file) => {
    try {
      const formData = new FormData();
      formData.append("image", file);

      const res = await fetch("http://localhost:5000/upload", {
        method: "POST",
        body: formData,
      });

      if (!res.ok) throw new Error("이미지 업로드 실패");

      const data = await res.json();
      if (!data.url) throw new Error("서버에서 URL을 받지 못함");

      return data.url;
    } catch (error) {
      console.error("uploadImageToServer error:", error);
      throw error;
    }
  };

  const allowedImageDomains = ["http://localhost:5000/uploads"];
  function sanitizeHtmlWithImageFilter(dirtyHtml) {
    DOMPurify.addHook("uponSanitizeElement", (node) => {
      if (node.tagName === "IMG") {
        const src = node.getAttribute("src") || "";
        const allowed = allowedImageDomains.some((domain) => src.startsWith(domain));
        if (!allowed) {
          node.parentNode && node.parentNode.removeChild(node);
        }
      }
    });

    const cleanHtml = DOMPurify.sanitize(dirtyHtml);
    DOMPurify.removeAllHooks();
    return cleanHtml;
  }

  const handleSubmit = async () => {
    if (token === null)
    {
      dispatch(showModal("login"));
      return;
    }
    try {
      const contentState = editorState.getCurrentContent();
      const rawContent = convertToRaw(contentState);

      const filteredEntityMap = {};
      for (const key in rawContent.entityMap) {
        if (key !== null && key !== "null" && rawContent.entityMap[key]) {
          filteredEntityMap[key] = rawContent.entityMap[key];
        }
      }

      for (const key in filteredEntityMap) {
        const entity = filteredEntityMap[key];
        if (entity.type === "IMAGE") {
          const localSrc = entity.data.src;
          const imgObj = imageFiles.find((img) => img.localUrl === localSrc);
          if (imgObj) {
            const serverUrl = await uploadImageToServer(imgObj.file);
            filteredEntityMap[key] = {
              ...entity,
              data: {
                ...entity.data,
                src: serverUrl,
              },
            };
          }
        }
      }

      const fixedRawContent = {
        ...rawContent,
        entityMap: filteredEntityMap,
      };

      const htmlContent = draftToHtml(fixedRawContent);
      const cleanHtml = sanitizeHtmlWithImageFilter(htmlContent);
      const titleValue = document.getElementById("draft-title").value;

      const res = await PostRequestWithAccessToken(token, "/post", {
        title: titleValue,
        content: cleanHtml,
      });

      alert("글 작성이 완료되었습니다!");
      setEditorState(EditorState.createEmpty());
      setImageFiles([]);
    } catch (error) {
      console.error("글 작성 실패:", error);
      alert("글 작성 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className={`flex flex-col w-full z-${z}`}>
      <input
        id="draft-title"
        placeholder="제목을 입력하세요"
        className={`mb-3 p-2 border border-gray-300 rounded focus:border-indigo-300 z-${z}`}
      />

      <div className={`border rounded-md mb-4 h-[590px] flex flex-col z-${z}`}>
        <Editor
          editorState={editorState}
          onEditorStateChange={onEditorStateChange}
          customBlockRenderFunc={imageBlockRenderer}
          wrapperClassName={`flex flex-col h-full z-${z}`}
          toolbarClassName={`!sticky top-0 z-${z} bg-white mb-2`}
          editorClassName={`flex-1 overflow-y-auto px-2`}
          toolbar={{
            options: [
              "inline",
              "colorPicker",
              "blockType",
              "list",
              "textAlign",
              "image",
              "emoji",
              "history",
              "fontFamily",
              "fontSize",
            ],
            inline: {
              options: ["bold", "italic", "underline", "strikethrough"],
            },
            blockType: { options: ["Normal", "Blockquote", "Code"] },
            fontSize: { options: [8, 9, 10, 11, 12, 14, 16, 18, 24, 30, 36] },
            fontFamily: {
              options: ["Arial", "Georgia", "Tahoma", "Times New Roman", "Verdana"],
            },
            list: { options: ["unordered", "ordered"] },
            textAlign: { options: ["left", "center", "right", "justify"] },
            colorPicker: {},
            emoji: {},
            image: {
              uploadCallback: uploadImageCallback,
              previewImage: true,
              alt: { present: true, mandatory: false },
              title: "이미지 업로드",
              uploadEnabled: true,
              urlEnabled: false,
            },
            remove: {},
            history: { options: ["undo", "redo"] },
          }}
        />
      </div>

      <div className="mt-auto flex justify-center">
        <button
          className="px-2 py-2 bg-indigo-500 text-xl rounded-full cursor-pointer hover:bg-indigo-700"
          onClick={handleSubmit}
        >
          🪶
        </button>
      </div>
    </div>
  );
};

export default PostTemp;
