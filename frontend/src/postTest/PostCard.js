import React, { useState, useEffect, useRef } from 'react';
import { Card, CardContent } from './ui/card';
import CommentsSection from './CommentsSection';

// 날짜 포맷팅 함수
function formatDateTime(timestamp) {
  if (!timestamp) return '';
  const date = new Date(timestamp);

  const yyyy = date.getFullYear();
  const mm = date.getMonth() + 1;
  const dd = date.getDate();

  let hh = date.getHours();
  const min = date.getMinutes().toString().padStart(2, '0');

  const ampm = hh < 12 ? '오전' : '오후';
  hh = hh % 12;
  if (hh === 0) hh = 12;

  return `${yyyy}. ${mm}. ${dd}. ${ampm} ${hh}:${min}`;
}

// HTML을 안전하게 자르되 <img> 태그는 제거하는 함수
function truncateHtmlSafely(html, maxLength) {
  const div = document.createElement('div');
  div.innerHTML = html;

  // 이미지 태그 제거
  const imgs = div.querySelectorAll('img');
  imgs.forEach((img) => img.remove());

  let currentLength = 0;

  function walk(node) {
    if (currentLength >= maxLength) return null;

    if (node.nodeType === Node.TEXT_NODE) {
      const remaining = maxLength - currentLength;
      const text = node.textContent.slice(0, remaining);
      currentLength += text.length;
      return document.createTextNode(text);
    }

    if (node.nodeType === Node.ELEMENT_NODE) {
      const clone = node.cloneNode(false);
      const children = Array.from(node.childNodes);

      for (let child of children) {
        const result = walk(child);
        if (result) clone.appendChild(result);
        if (currentLength >= maxLength) break;
      }

      return clone;
    }

    return null;
  }

  const result = walk(div);
  const container = document.createElement('div');
  if (result) container.appendChild(result);

  return container.innerHTML;
}

const COMMENTS_FIXED_HEIGHT = 384; // px

export default function PostCard({ post }) {
  const [expanded, setExpanded] = useState(false);
  const [showComments, setShowComments] = useState(false);
  const [repliesToggledMap, setRepliesToggledMap] = useState({});
  const [animatedHeight, setAnimatedHeight] = useState(0);
  const [needsTruncation, setNeedsTruncation] = useState(false);

  const previewRef = useRef(null);
  const fullContentRef = useRef(null);

  const {
    author_nickname: nickname,
    title,
    content_html: contentHtml,
    thumbnail_img_url: thumbnailImage,
    comments = [],
    created_at,
    updated_at,
  } = post;

  const createdAt = created_at ? created_at * 1000 : null;
  const updatedAt = updated_at !== null && updated_at !== undefined ? updated_at * 1000 : null;
  const isModified = updatedAt && updatedAt !== createdAt;

  // 미리보기용 HTML: 이미지 태그 제거
  const previewHtml = truncateHtmlSafely(contentHtml, 10000);

  // 토글 함수
  function toggleExpanded() {
    setExpanded((prev) => !prev);
  }
  function onToggleClick(e) {
    e.stopPropagation();
    toggleExpanded();
  }
  function handleRepliesToggle(commentId, isOpen) {
    setRepliesToggledMap((prev) => ({
      ...prev,
      [commentId]: isOpen,
    }));
  }

  useEffect(() => {
    if (showComments) {
      setAnimatedHeight(0);
      const timeoutId = setTimeout(() => {
        setAnimatedHeight(COMMENTS_FIXED_HEIGHT);
      }, 10);
      return () => clearTimeout(timeoutId);
    } else {
      setAnimatedHeight(0);
    }
  }, [showComments]);

  // 본문 미리보기 높이 체크 (이미지 제외한 부분)
  useEffect(() => {
    if (previewRef.current) {
      const height = previewRef.current.scrollHeight;
      setNeedsTruncation(height > 200);
    }
  }, [previewHtml]);

  // 전체 본문(이미지 포함) 높이 체크 (이미지 로딩 완료 후)
  useEffect(() => {
    if (!fullContentRef.current) return;

    const imgs = fullContentRef.current.querySelectorAll('img');
    if (imgs.length === 0) {
      // 이미지 없으면 바로 체크
      setNeedsTruncation(fullContentRef.current.scrollHeight > 200);
      return;
    }

    let loadedCount = 0;
    const onLoadOrError = () => {
      loadedCount += 1;
      if (loadedCount === imgs.length) {
        setNeedsTruncation(fullContentRef.current.scrollHeight > 200);
      }
    };

    imgs.forEach((img) => {
      if (img.complete) {
        onLoadOrError();
      } else {
        img.addEventListener('load', onLoadOrError);
        img.addEventListener('error', onLoadOrError);
      }
    });

    return () => {
      imgs.forEach((img) => {
        img.removeEventListener('load', onLoadOrError);
        img.removeEventListener('error', onLoadOrError);
      });
    };
  }, [contentHtml]);

  return (
    <Card className="mb-6 transition-all duration-300">
      <CardContent>
        <div className="mb-2 text-sm text-gray-600">
          <span className="font-semibold">{nickname}</span> ·{' '}
          <span>{createdAt ? formatDateTime(createdAt) : ''}</span>
          {isModified && (
            <span className="ml-2 text-xs text-gray-400">
              (수정됨: {formatDateTime(updatedAt)})
            </span>
          )}
        </div>

        <h2 className="text-lg font-bold mb-2">{title}</h2>

        {/* 썸네일 이미지는 접기 상태이고, 본문 길이가 길 때만 보여줌 */}
        {!expanded && needsTruncation && thumbnailImage && (
          <div className="flex justify-center mb-2">
            <img
              src={thumbnailImage}
              alt="thumbnail"
              className="rounded max-h-64 object-cover"
            />
          </div>
        )}

        <div
          className={`prose prose-sm max-w-none text-gray-800 cursor-pointer select-text group ${
            !expanded && needsTruncation ? 'line-clamp-6' : ''
          }`}
          onClick={toggleExpanded}
          title={needsTruncation ? (expanded ? '접기' : '더보기') : undefined}
          aria-expanded={expanded}
          ref={previewRef}
        >
          <div
            dangerouslySetInnerHTML={{
              __html: expanded ? contentHtml : previewHtml,
            }}
          />
        </div>

        {/* 더보기/접기 표시 */}
        {needsTruncation && (
          <span
            onClick={onToggleClick}
            className="text-blue-500 ml-1 cursor-pointer group-hover:underline"
            role="button"
            tabIndex={0}
            onKeyPress={(e) => {
              if (e.key === 'Enter') onToggleClick(e);
            }}
          >
            {expanded ? '접기' : '... 더보기'}
          </span>
        )}

        <div className="mt-4 flex justify-between">
          <div className="flex space-x-2">
            <button onClick={() => alert('추천!')}>👍 추천</button>
            <button
              onClick={() => alert('비추천!')}
              className="bg-red-500 hover:bg-red-600"
            >
              👎 비추천
            </button>
          </div>

          <button
            onClick={() => setShowComments((prev) => !prev)}
            className="bg-gray-500 hover:bg-gray-600"
          >
            {showComments ? '💬 댓글 닫기' : '💬 댓글 보기'}
          </button>
        </div>

        <div
          style={{
            height: animatedHeight,
            transition: 'height 300ms ease',
            overflow: 'hidden',
          }}
          className="mt-4"
          aria-expanded={showComments}
        >
          {showComments && (
            <CommentsSection comments={comments} onToggle={handleRepliesToggle} />
          )}
        </div>

        {/* 보이지 않는 전체 본문 렌더링 (이미지 포함) - 높이 측정 용도 */}
        <div
          ref={fullContentRef}
          className="invisible absolute left-[-9999px] top-0 w-[calc(100%-2rem)] max-w-none"
          dangerouslySetInnerHTML={{ __html: contentHtml }}
          aria-hidden="true"
        />
      </CardContent>
    </Card>
  );
}
