import React, { useState } from "react";
import { Editor } from "react-draft-wysiwyg";
import { EditorState, convertToRaw } from "draft-js";
import draftToHtml from "draftjs-to-html";
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import DOMPurify from "dompurify";

import PostRequestWithAccessToken from "../api/axios/requestWithAccessToken";
import { useSelector } from "react-redux";

const PostTemp = () => {
  const [editorState, setEditorState] = useState(EditorState.createEmpty());
  const [imageFiles, setImageFiles] = useState([]);
  const token = useSelector((state) => state.auth.accessToken);

  const onEditorStateChange = (state) => {
    setEditorState(state);
  };

  const uploadImageCallback = (file) => {
    const localUrl = URL.createObjectURL(file);
    setImageFiles((prev) => [...prev, { file, localUrl }]);
    return Promise.resolve({ data: { link: localUrl } });
  };

  const uploadImageToServer = async (file) => {
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
  };

  const allowedImageDomains = ["http://localhost:5000/uploads"];

  function sanitizeHtmlWithImageFilter(dirtyHtml) {
    DOMPurify.addHook("uponSanitizeElement", (node) => {
      if (node.tagName === "IMG") {
        const src = node.getAttribute("src") || "";
        const allowed = allowedImageDomains.some((domain) =>
          src.startsWith(domain)
        );
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
    try {
      const contentState = editorState.getCurrentContent();
      let rawContent = convertToRaw(contentState);
      const entityMap = rawContent.entityMap;
      const urlMap = {};

      for (let key in entityMap) {
        const entity = entityMap[key];
        if (entity.type === "IMAGE") {
          const localSrc = entity.data.src;
          const imgObj = imageFiles.find((img) => img.localUrl === localSrc);

          if (imgObj) {
            const serverUrl = await uploadImageToServer(imgObj.file);
            urlMap[localSrc] = serverUrl;
            entityMap[key].data.src = serverUrl;
          }
        }
      }

      const htmlContent = draftToHtml(rawContent);
      const imgFiltered = sanitizeHtmlWithImageFilter(htmlContent);

      console.log("HTML");
      console.log(imgFiltered);

      const res = await PostRequestWithAccessToken(token, "/post", {
        title: document.getElementById("draft-title").value,
        content: imgFiltered,
      });

      console.log("작성 완료:", res.data);

      alert("글 작성이 완료되었습니다!");
      setEditorState(EditorState.createEmpty());
      setImageFiles([]);
    } catch (error) {
      console.error("글 작성 실패:", error);
      alert("글 작성 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="flex flex-col w-full">
      {/* 제목 입력창 */}
      <input
        id="draft-title"
        placeholder="제목을 입력하세요"
        className="mb-3 p-2 border border-gray-300 rounded"
      />

      {/* 에디터 */}
      <div className="border rounded-md mb-4 h-[500px] flex flex-col">
        <Editor
          editorState={editorState}
          onEditorStateChange={onEditorStateChange}
          wrapperClassName="flex flex-col h-full"
          toolbarClassName="!sticky top-0 z-10 bg-white mb-2"
          editorClassName="flex-1 overflow-y-auto px-2"
          toolbar={{
            options: [ 
              "inline",
              "colorPicker",
              "list",
              "textAlign",
              "emoji",
              "image",
              "remove",
              "history",
              "blockType",
              "fontFamily",
              "fontSize",
            ],
            inline: {
              options: [
                "bold",
                "italic",
                "underline",
                "strikethrough",
                "monospace",
              ],
            },
            blockType: {
              options: [
                "Normal",
                "Blockquote",
                "Code",
              ],
            },
            fontSize: {
              options: [
                8, 9, 10, 11, 12, 14, 16, 18, 24, 30, 36, 48, 60, 72, 96,
              ],
            },
            fontFamily: {
              options: [
                "Arial",
                "Georgia",
                "Impact",
                "Tahoma",
                "Times New Roman",
                "Verdana",
              ],
            },
            list: {
              options: ["unordered", "ordered"],
            },
            textAlign: {
              options: ["left", "center", "right", "justify"],
            },
            colorPicker: {},
            link: {
              options: ["link", "unlink"],
            },
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
            history: {
              options: ["undo", "redo"],
            },
          }}
        />
      </div>

      {/* 버튼 */}
      <div className="mt-auto flex justify-center">
  <button
    className="
      px-2
      py-2
      bg-indigo-500
      text-xl
      rounded-full
      cursor-pointer
      hover:bg-indigo-700
    "
    onClick={handleSubmit}
  >
    🪶
  </button>
</div>
    </div>
  );
};

export default PostTemp;
