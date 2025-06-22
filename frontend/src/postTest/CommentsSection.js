import React, { useState, useRef, useEffect } from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import PostRequestWithAccessToken from '../api/axios/requestWithAccessToken';

export default function CommentsSection({
  comments,
  onToggle,
  postId,
  isLoading,
  replyTo,
  setReplyTo,
}) {
  const [newCommentText, setNewCommentText] = useState('');
  const accessToken = useSelector((state) => state.auth.accessToken);

  const handleWriteComment = async () => {
    if (!newCommentText.trim()) return;
    try {
      const payload = {
        content: newCommentText,
      };
      let url = '/comment/' + postId;

      if (replyTo?.commentId) {
        url += '/' + replyTo.commentId;
      }

      await PostRequestWithAccessToken(accessToken, url, payload);
      setNewCommentText('');
      setReplyTo(null);
      // TODO: 작성 후 댓글 목록 갱신 필요 시 상위에서 처리
    } catch (error) {
      console.error('댓글/답글 작성 실패', error);
    }
  };

  return (
    <div className="p-4 border rounded bg-gray-50 overflow-y-auto max-h-96 flex flex-col" style={{ height: '100%' }}>
      <h3 className="text-sm font-semibold mb-2">댓글</h3>

      <ul className="text-sm text-gray-700 space-y-6 flex-grow overflow-y-auto mb-4 pr-2">
        {comments.map((comment) => (
          <CommentItem
            key={comment.comment_id}
            comment={comment}
            onToggle={onToggle}
            onReply={() =>
              setReplyTo({
                commentId: comment.comment_id,
                authorNickname: comment.author_nickname,
                commentContent: comment.content,
              })
            }
            postId={postId}
          />
        ))}
      </ul>

      <div className="pt-3 border-t border-gray-300">
        {replyTo && (
          <div className="mb-2 px-3 py-2 bg-gray-100 border border-gray-300 rounded text-sm text-gray-800 relative">
            <div className="mb-1 font-semibold">{replyTo.authorNickname}</div>
            <div className="text-gray-600 italic break-words line-clamp-2">
              {replyTo.commentContent}
            </div>
            <button
              onClick={() => setReplyTo(null)}
              className="absolute top-2 right-2 text-xs text-red-500 hover:underline"
            >
              ✕ 취소
            </button>
          </div>
        )}

        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleWriteComment();
          }}
          className="flex items-center"
        >
          <div className="flex-grow relative">
            <div className="flex w-full border rounded overflow-hidden">
              {replyTo && (
                <span className="bg-gray-200 text-gray-700 text-sm px-2 flex items-center whitespace-nowrap">
                  @{replyTo.authorNickname}
                </span>
              )}
              <input
                type="text"
                value={newCommentText}
                onChange={(e) => setNewCommentText(e.target.value)}
                placeholder={replyTo ? '답글을 입력하세요...' : '댓글을 입력하세요'}
                className="flex-grow px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
                style={{
                  borderTopLeftRadius: replyTo ? 0 : '0.375rem',
                  borderBottomLeftRadius: replyTo ? 0 : '0.375rem',
                }}
              />
            </div>
          </div>
          <button
            type="submit"
            className={`ml-2 bg-blue-500 hover:bg-blue-600 text-white rounded px-4 py-2 text-sm ${
              newCommentText.trim() ? 'cursor-pointer' : 'cursor-not-allowed opacity-50'
            }`}
            disabled={!newCommentText.trim()}
          >
            쓰기
          </button>
        </form>
      </div>
    </div>
  );
}

function CommentItem({ comment, onToggle, onReply, postId }) {
  const [showReplies, setShowReplies] = useState(false);
  const [replies, setReplies] = useState([]);
  const [loadingReplies, setLoadingReplies] = useState(false);
  const [createdAtCursor, setCreatedAtCursor] = useState(null);
  const [replyIdCursor, setReplyIdCursor] = useState(null);
  const [newlyAddedReplyId, setNewlyAddedReplyId] = useState(null);

  const repliesStartRef = useRef(null);

  const {
    comment_id,
    author_nickname,
    content,
    created_at,
    reply_count = 0,
  } = comment;

  const fetchMoreReplies = async () => {
    if (loadingReplies) return;
    setLoadingReplies(true);

    try {
      const params = {};
      if (createdAtCursor) params.createdAtCursor = createdAtCursor;
      if (replyIdCursor) params.replyIdCursor = replyIdCursor;

      const url = `http://localhost:8080/dumbo-backend/comment/${postId}/${comment_id}`;
      const response = await axios.get(url, { params });

      const newReplies = response.data.data || [];

      if (newReplies.length > 0) {
        setReplies((prev) => [...prev, ...newReplies]);
        setNewlyAddedReplyId(newReplies[0].comment_id);

        const last = newReplies[newReplies.length - 1];
        setCreatedAtCursor(last.created_at);
        setReplyIdCursor(last.comment_id);

        // 새로 불러온 첫 답글로 스크롤 이동
        setTimeout(() => {
          repliesStartRef.current?.scrollIntoView({ behavior: 'smooth' });
        }, 100);
      }
    } catch (error) {
      console.error('답글 더보기 실패', error);
    } finally {
      setLoadingReplies(false);
    }
  };

  const handleToggleReplies = () => {
    const next = !showReplies;
    setShowReplies(next);
    if (next && replies.length === 0) {
      fetchMoreReplies();
    }
    if (onToggle) onToggle(comment_id, next);
  };

  // 남은 답글 개수 계산 (reply_count - 불러온 답글 수)
  const remainingRepliesCount = reply_count - replies.length;

  return (
    <li>
      <div className="flex items-center space-x-2 mb-1 text-gray-800 font-semibold text-base">
        <span>{author_nickname}</span>
        <span className="text-xs text-gray-400">
          {new Date(created_at * 1000).toLocaleString()}
        </span>
      </div>

      <div
        className="mb-2 text-gray-700 cursor-pointer hover:bg-gray-100 p-1 rounded"
        onClick={onReply}
      >
        {content}
      </div>

      {/* 답글이 있고, 답글 리스트가 닫혀있으면 */}
      {reply_count > 0 && !showReplies && (
        <button
          onClick={handleToggleReplies}
          className="text-sm text-blue-500 hover:underline"
          type="button"
          disabled={loadingReplies}
        >
          {loadingReplies ? '로딩 중...' : `└  답글 ${reply_count}개`}
        </button>
      )}

      {/* 답글 리스트 열렸을 때 */}
      {showReplies && (
        <>
          <ul className="ml-6 mt-3 space-y-4 border-l-2 border-gray-300 pl-4">
            {replies.map((reply) => (
              <li
                key={reply.comment_id}
                ref={reply.comment_id === newlyAddedReplyId ? repliesStartRef : null}
              >
                <div className="flex items-center space-x-2 text-gray-800 font-semibold text-base">
                  <span>{reply.author_nickname}</span>
                  <span className="text-xs text-gray-400">
                    {new Date(reply.created_at * 1000).toLocaleString()}
                  </span>
                </div>
                <div className="text-gray-700">{reply.content}</div>
              </li>
            ))}
          </ul>

          {/* 남은 답글이 있으면 답글 더보기 버튼 표시 */}
          {remainingRepliesCount > 0 && (
            <button
              onClick={fetchMoreReplies}
              className="text-sm text-blue-500 hover:underline mt-2"
              disabled={loadingReplies}
              type="button"
            >
              {loadingReplies ? '로딩 중...' : `+ 답글 ${remainingRepliesCount}개`}
            </button>
          )}

          {/* 답글 숨기기 버튼은 오른쪽 정렬 */}
          <div className="flex justify-end mt-2">
            <button
              onClick={handleToggleReplies}
              className="text-sm text-blue-500 hover:underline"
              type="button"
            >
              답글 숨기기
            </button>
          </div>
        </>
      )}
    </li>
  );
}
