import React, { useState, useEffect, useRef } from 'react';
import { useSelector } from "react-redux";
import { Card, CardContent } from './ui/card';
import CommentsSection from './CommentsSection';
import VoteBar from './VoteBar';
import { DeleteRequestWithAccessToekn, GetRequestWithAccessToken } from '../api/axios/requestWithAccessToken';

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

function truncateHtmlSafely(html, maxLength) {
  const div = document.createElement('div');
  div.innerHTML = html;
  div.querySelectorAll('img').forEach((img) => img.remove());

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
      for (let child of node.childNodes) {
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

const COMMENTS_FIXED_HEIGHT = 384;

export default function PostCard({ post }) {
  const [expanded, setExpanded] = useState(false);
  const [showComments, setShowComments] = useState(false);
  const [comments, setComments] = useState([]);
  const [commentsLoaded, setCommentsLoaded] = useState(false);
  const [isLoadingComments, setIsLoadingComments] = useState(false);
  const [repliesToggledMap, setRepliesToggledMap] = useState({});
  const [animatedHeight, setAnimatedHeight] = useState(0);
  const [needsTruncation, setNeedsTruncation] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [isTransitioning, setIsTransitioning] = useState(false);
  const [replyTo, setReplyTo] = useState(null); // ✅ 추가: 답글 상태

  const deleteTimeoutRef = useRef(null);
  const previewRef = useRef(null);
  const fullContentRef = useRef(null);

  const {
    post_id: postId,
    author_id: authorId,
    author_nickname: nickname,
    title,
    content_html: contentHtml,
    thumbnail_img_url: thumbnailImage,
    created_at,
    updated_at,
    likes = 0,
    dislikes = 0,
  } = post;

  const createdAt = created_at ? created_at * 1000 : null;
  const updatedAt = updated_at !== null && updated_at !== undefined ? updated_at * 1000 : null;
  const isModified = updatedAt && updatedAt !== createdAt;

  const previewHtml = truncateHtmlSafely(contentHtml, 10000);

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

  useEffect(() => {
    if (previewRef.current) {
      const height = previewRef.current.scrollHeight;
      setNeedsTruncation(height > 200);
    }
  }, [previewHtml]);

  useEffect(() => {
    if (!fullContentRef.current) return;

    const imgs = fullContentRef.current.querySelectorAll('img');
    if (imgs.length === 0) {
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
      if (img.complete) onLoadOrError();
      else {
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

  const userId = useSelector(state => state.auth.userId);
  const accessToken = useSelector(state => state.auth.accessToken);

  const handleDelete = async () => {
    const res = await DeleteRequestWithAccessToekn(accessToken, "/post/" + postId);
    console.log(res.data);
  };

  const handleDeleteClick = () => {
    if (isTransitioning) return;

    if (!confirmDelete) {
      setIsTransitioning(true);
      setConfirmDelete(true);
      deleteTimeoutRef.current = setTimeout(() => {
        setConfirmDelete(false);
      }, 3000);
      setTimeout(() => {
        setIsTransitioning(false);
      }, 500);
    } else {
      handleDelete();
    }
  };

  useEffect(() => {
    return () => clearTimeout(deleteTimeoutRef.current);
  }, []);

  const handleToggleComments = async () => {
    setShowComments(prev => !prev);

    if (!commentsLoaded && !isLoadingComments) {
      setIsLoadingComments(true);
      try {
        const res = await GetRequestWithAccessToken(accessToken, `/comment/${postId}`);
        setComments(res.data.data || []);
        setCommentsLoaded(true);
      } catch (e) {
        console.error("댓글 불러오기 실패", e);
      } finally {
        setIsLoadingComments(false);
      }
    }
  };

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

        <div className="mt-4 flex items-center space-x-4">
          {userId === authorId ? (
            <div className="flex space-x-2">
              <button
                onClick={() => alert('글 수정!')}
                className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
              >
                ✏️ 수정
              </button>
              <div className="relative">
                <button
                  onClick={handleDeleteClick}
                  className={`text-white px-3 py-1 rounded whitespace-nowrap overflow-hidden transition-all duration-500 ease-in-out
                    ${confirmDelete ? 'bg-red-600 w-[260px]' : 'bg-gray-700 w-[120px] hover:bg-gray-800'}
                    ${isTransitioning ? 'pointer-events-none opacity-70' : ''}`}
                  disabled={isTransitioning}
                >
                  {confirmDelete ? '🛑 진짜로 글을 삭제하시겠어요?' : '🗑️ 삭제'}
                </button>
              </div>
            </div>
          ) : (
            <div className="flex space-x-2">
              <button
                onClick={() => alert('추천!')}
                className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
              >
                👍 추천
              </button>
              <button
                onClick={() => alert('비추천!')}
                className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
              >
                👎 비추천
              </button>
            </div>
          )}
          <VoteBar likes={likes} dislikes={dislikes} className="w-40 h-3" />
        </div>

        <div className="mt-2">
          <button
            onClick={handleToggleComments}
            className="bg-gray-500 hover:bg-gray-600 text-white px-3 py-1 rounded"
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
            <CommentsSection
              comments={comments}
              onToggle={handleRepliesToggle}
              postId={postId}
              isLoading={isLoadingComments}
              replyTo={replyTo}           // ✅ 추가
              setReplyTo={setReplyTo}     // ✅ 추가
            />
          )}
        </div>

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
