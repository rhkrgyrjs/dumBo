import React, { useState, useRef, useEffect } from 'react';
import { Card, CardContent } from './ui/card';
import { Button } from './ui/button';
import CommentsSection from './CommentsSection';

const COMMENTS_FIXED_HEIGHT = 384; // px

export default function PostCard({ post }) {
  const [expanded, setExpanded] = useState(false);
  const [showComments, setShowComments] = useState(false);
  const [repliesToggledMap, setRepliesToggledMap] = useState({});

  const [animatedHeight, setAnimatedHeight] = useState(0);
  const animationTimeoutRef = useRef(null);

  const {
    nickname,
    title,
    createdAt,
    updatedAt,
    contentHtml,
    thumbnailImage,
    comments = [],
  } = post;

  const isModified = updatedAt && updatedAt !== createdAt;

  // HTML 태그 제거용 임시 div
  const tempDiv = document.createElement('div');
  tempDiv.innerHTML = contentHtml;
  const plainText = tempDiv.textContent || tempDiv.innerText || '';

  const isContentTruncated = plainText.length > 300;
  const truncatedText = plainText.slice(0, 300);

  useEffect(() => {
    if (showComments) {
      setAnimatedHeight(0);
      animationTimeoutRef.current = setTimeout(() => {
        setAnimatedHeight(COMMENTS_FIXED_HEIGHT);
      }, 10);
    } else {
      setAnimatedHeight(0);
    }
    return () => clearTimeout(animationTimeoutRef.current);
  }, [showComments]);

  function handleRepliesToggle(commentId, isOpen) {
    setRepliesToggledMap((prev) => ({
      ...prev,
      [commentId]: isOpen,
    }));
  }

  function toggleExpanded() {
    if (isContentTruncated) {
      setExpanded((prev) => !prev);
    }
  }

  function onToggleClick(e) {
    e.stopPropagation();
    toggleExpanded();
  }

  return (
    <Card className="mb-6 transition-all duration-300">
      <CardContent>
        <div className="mb-2 text-sm text-gray-600">
          <span className="font-semibold">{nickname}</span> ·{' '}
          <span>{new Date(createdAt).toLocaleString()}</span>
          {isModified && (
            <span className="ml-2 text-xs text-gray-400">(수정됨)</span>
          )}
        </div>

        <h2 className="text-lg font-bold mb-2">{title}</h2>

        {thumbnailImage && (
          <img
            src={thumbnailImage}
            alt="thumbnail"
            className="mb-2 w-full rounded max-h-64 object-cover"
          />
        )}

        <p
          onClick={toggleExpanded}
          className={`text-sm text-gray-800 cursor-pointer select-text group`}
          aria-expanded={expanded}
          title={isContentTruncated ? (expanded ? '접기' : '더보기') : undefined}
        >
          {expanded ? (
            <>
              {plainText}
              <span
                onClick={onToggleClick}
                className="text-blue-500 ml-1 cursor-pointer group-hover:underline"
              >
                접기
              </span>
            </>
          ) : (
            <>
              {truncatedText}...
              <span
                onClick={onToggleClick}
                className="text-blue-500 ml-1 cursor-pointer group-hover:underline"
              >
                더보기
              </span>
            </>
          )}
        </p>

        <div className="mt-4 flex space-x-2">
          <Button onClick={() => alert('추천!')}>👍 추천</Button>
          <Button
            onClick={() => alert('비추천!')}
            className="bg-red-500 hover:bg-red-600"
          >
            👎 비추천
          </Button>
          <Button
            onClick={() => setShowComments((prev) => !prev)}
            className="bg-gray-500 hover:bg-gray-600"
          >
            {showComments ? '💬 댓글 닫기' : '💬 댓글 보기'}
          </Button>
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
      </CardContent>
    </Card>
  );
}
