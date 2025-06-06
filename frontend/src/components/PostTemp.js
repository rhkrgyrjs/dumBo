import React, { useState } from "react"; // React와 useState 훅을 불러옴
import { Editor } from "react-draft-wysiwyg"; // WYSIWYG 에디터 컴포넌트 임포트
import { EditorState, convertToRaw } from "draft-js"; // 에디터 상태 관리 및 내용을 raw 객체로 변환하는 함수
import draftToHtml from "draftjs-to-html"; // draft-js의 raw content를 HTML 문자열로 변환해주는 라이브러리
import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css"; // 에디터 스타일시트 임포트
import DOMPurify from "dompurify"; // 서버로 날릴 HTML의 보안 위협을 막기 위해

import request from "../api/axios/request";

const PostTemp = () => {
  // 에디터 상태를 관리 (초기값은 빈 에디터 상태)
  const [editorState, setEditorState] = useState(EditorState.createEmpty());

  // 업로드한 이미지 파일 목록을 상태로 관리, 각 아이템은 {file, localUrl} 객체 형태
  const [imageFiles, setImageFiles] = useState([]);

  // 에디터 상태가 변할 때마다 호출되는 콜백 함수
  const onEditorStateChange = (state) => {
    setEditorState(state); // 변경된 에디터 상태로 업데이트
  };

  // 이미지 업로드 콜백 함수 (에디터 내 이미지 삽입시 호출됨)
  // 여기서는 서버 업로드 대신 브라우저 내 임시 로컬 URL을 생성하여 미리보기용으로 사용
  const uploadImageCallback = (file) => {
    // File 객체에서 로컬 URL 생성 (브라우저 내 임시 URL)
    const localUrl = URL.createObjectURL(file);

    // 이미지 파일 상태 배열에 새 이미지 추가
    setImageFiles((prev) => [...prev, { file, localUrl }]);

    // Promise 형태로 {data: {link: localUrl}} 반환해야 에디터가 인식함
    return Promise.resolve({ data: { link: localUrl } });
  };

  // 실제 서버로 이미지를 업로드하는 함수 (handleSubmit에서 사용)
  const uploadImageToServer = async (file) => {
    // FormData 객체 생성, key: "image", value: 파일
    const formData = new FormData();
    formData.append("image", file);

    // 이미지 업로드 API 호출 (POST)
    const res = await fetch("http://localhost:5000/upload", {
      method: "POST",
      body: formData,
    });

    // HTTP 응답 상태가 OK가 아니면 에러 던짐
    if (!res.ok) throw new Error("이미지 업로드 실패");

    // JSON 응답 파싱
    const data = await res.json();

    // 서버에서 URL을 받지 못하면 에러 던짐
    if (!data.url) throw new Error("서버에서 URL을 받지 못함");

    // 서버에서 받은 이미지 URL 반환
    return data.url;
  };

  // 허용할 이미지 도메인 배열 (예시)
const allowedImageDomains = [
  "http://localhost:5000/uploads"
];

// HTML 정리 함수 예시
function sanitizeHtmlWithImageFilter(dirtyHtml) {
  // DOMPurify의 hook을 이용해 이미지 src 필터링
  DOMPurify.addHook("uponSanitizeElement", (node, data) => {
    if (node.tagName === "IMG") {
      const src = node.getAttribute("src") || "";
      const allowed = allowedImageDomains.some((domain) => src.startsWith(domain));
      if (!allowed) {
        // 허용 도메인이 아니면 이미지 태그 자체를 제거
        node.parentNode && node.parentNode.removeChild(node);
      }
    }
  });

  const cleanHtml = DOMPurify.sanitize(dirtyHtml);

  // hook 제거(중복 방지)
  DOMPurify.removeAllHooks();

  return cleanHtml;
}

  // 글 작성 완료 시 호출되는 함수
  const handleSubmit = async () => {
    try {
      // 에디터의 현재 내용(ContentState) 가져오기
      const contentState = editorState.getCurrentContent();

      // contentState를 raw JS 객체로 변환 (entityMap 포함)
      let rawContent = convertToRaw(contentState);

      // rawContent 내 entityMap (이미지 등 리소스 정보 포함)
      const entityMap = rawContent.entityMap;

      // 로컬 이미지 URL과 서버 업로드 후 URL을 매핑할 객체
      const urlMap = {};

      // entityMap을 순회하며 이미지 엔티티를 찾음
      for (let key in entityMap) {
        const entity = entityMap[key];

        // 엔티티 타입이 IMAGE인 경우에만 처리
        if (entity.type === "IMAGE") {
          const localSrc = entity.data.src; // 로컬 URL 추출

          // imageFiles 상태에서 로컬 URL과 일치하는 이미지 객체 찾기
          const imgObj = imageFiles.find((img) => img.localUrl === localSrc);

          if (imgObj) {
            // 서버로 이미지 업로드 후 URL 받기
            const serverUrl = await uploadImageToServer(imgObj.file);

            // URL 매핑 저장 (로컬 URL -> 서버 URL)
            urlMap[localSrc] = serverUrl;

            // entityMap 내 이미지 src를 서버 URL로 교체
            entityMap[key].data.src = serverUrl;
          }
        }
      }

      // entityMap이 바뀐 rawContent를 HTML로 변환
      const htmlContent = draftToHtml(rawContent);

      // 디버깅용 콘솔 출력 (로컬 URL과 서버 URL 매핑 정보)
      console.log("이미지 URL 맵:", urlMap);
      // 변환된 HTML 내용 출력
      console.log("작성된 HTML:", htmlContent);

      // 이미지 출처도 한번 거르기============================================
      const imgFiltered = sanitizeHtmlWithImageFilter(htmlContent);

      console.log("안전하게 변환된 HTML", imgFiltered);

      let res = request.post('/post/draft', {'title' : '임시제목', 'content' : imgFiltered});
console.log(res.data);

      // 완료 메시지 표시
      alert("글 작성이 완료되었습니다!");

      // 에디터 초기화 (빈 상태로 리셋)
      setEditorState(EditorState.createEmpty());

      // 이미지 파일 상태도 초기화
      setImageFiles([]);
    } catch (error) {
      // 에러 발생 시 콘솔 출력 및 알림
      console.error("글 작성 실패:", error);
      alert("글 작성 중 오류가 발생했습니다.");
    }
  };

  // 이미지 블록 렌더러 함수 (에디터가 특정 블록을 어떻게 렌더할지 결정)
  const imageBlockRenderer = (contentBlock) => {
    // atomic 타입 블록이면 이미지 컴포넌트로 렌더링
    if (contentBlock.getType() === "atomic") {
      return {
        component: MediaComponent, // 이미지 렌더링 컴포넌트 지정
        editable: false, // 이미지 블록은 편집 불가
      };
    }
    // atomic 블록이 아니면 기본 렌더러 사용
    return null;
  };

  // 이미지 렌더링 컴포넌트 정의
  const MediaComponent = (props) => {
    // 에디터 상태에서 엔티티 데이터 가져오기
    const entity = props.contentState.getEntity(props.block.getEntityAt(0));
    const { src, alt, height, width } = entity.getData(); // src 등 이미지 정보 추출

    // img 태그로 이미지 렌더링, 스타일로 크기 조절 가능
    return (
      <img
        src={src}
        alt={alt || ""}
        style={{ height: height || "auto", width: width || "auto" }}
      />
    );
  };

  return (
    <div style={{ maxWidth: 800, margin: "auto" }}>
      <h2>게시글 작성</h2>

      {/* 에디터 컴포넌트 */}
      <Editor
        editorState={editorState} // 현재 에디터 상태 전달
        onEditorStateChange={onEditorStateChange} // 상태 변경 콜백 지정
        wrapperClassName="demo-wrapper" // 에디터 래퍼 CSS 클래스 지정
        editorClassName="demo-editor" // 에디터 영역 CSS 클래스 지정

        // 툴바 옵션 설정 (한글 타이틀 포함)
        toolbar={{
          options: [
            "inline",
            "blockType",
            "fontSize",
            "fontFamily",
            "list",
            "textAlign",
            "colorPicker",
            "link",
            "emoji",
            "image",
            "remove",
            "history",
          ],
          inline: {
            options: ["bold", "italic", "underline", "strikethrough", "monospace"],
            bold: { title: "굵게" },
            italic: { title: "기울임" },
            underline: { title: "밑줄" },
            strikethrough: { title: "취소선" },
            monospace: { title: "고정폭" },
          },
          blockType: {
            options: [
              "Normal",
              "H1",
              "H2",
              "H3",
              "H4",
              "H5",
              "H6",
              "Blockquote",
              "Code",
            ],
            dropdownClassName: "rdw-dropdown",
            normal: { label: "기본" },
            h1: { label: "제목1" },
            h2: { label: "제목2" },
            h3: { label: "제목3" },
            h4: { label: "제목4" },
            h5: { label: "제목5" },
            h6: { label: "제목6" },
            blockquote: { label: "인용문" },
            code: { label: "코드" },
          },
          fontSize: {
            options: [8, 9, 10, 11, 12, 14, 16, 18, 24, 30, 36, 48, 60, 72, 96],
            title: "글자 크기",
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
            title: "글꼴",
          },
          list: {
            options: ["unordered", "ordered"],
            unordered: { title: "글머리 기호" },
            ordered: { title: "번호 매기기" },
          },
          textAlign: {
            options: ["left", "center", "right", "justify"],
            left: { title: "왼쪽 정렬" },
            center: { title: "가운데 정렬" },
            right: { title: "오른쪽 정렬" },
            justify: { title: "양쪽 정렬" },
          },
          colorPicker: { title: "글자 색상" },
          link: {
            options: ["link", "unlink"],
            link: { title: "링크 삽입" },
            unlink: { title: "링크 제거" },
          },
          emoji: { title: "이모지" },
          image: {
            uploadCallback: uploadImageCallback, // 이미지 업로드 시 호출되는 콜백 (로컬 미리보기용)
            previewImage: true, // 미리보기 기능 활성화
            alt: { present: true, mandatory: false }, // alt 태그 입력 옵션 활성화
            title: "이미지 업로드", // 이미지 버튼 툴팁
            uploadEnabled: true,   // 로컬 업로드 허용
            urlEnabled: false,     // URL 삽입 비활성화
          },
          remove: { title: "서식 제거" },
          history: {
            options: ["undo", "redo"],
            undo: { title: "되돌리기" },
            redo: { title: "되돌리기 취소" },
          },
        }}

        // 이미지 atomic 블록에 대해 커스텀 렌더링 함수 지정
        customBlockRenderFunc={imageBlockRenderer}
      />

      {/* 글 업로드 버튼 */}
      <button
        style={{
          marginTop: 20,
          padding: "10px 20px",
          backgroundColor: "#4F46E5",
          color: "white",
          border: "none",
          borderRadius: 5,
          cursor: "pointer",
        }}
        onClick={handleSubmit} // 클릭 시 글 작성 완료 함수 실행
      >
        글 업로드
      </button>
      <button onClick={() => {console.log(request.post('post/draft').data)}}>테스트</button>
    </div>
  );
};

export default PostTemp;
