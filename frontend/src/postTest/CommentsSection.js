import React, { useState } from 'react';
import { useSelector } from "react-redux";
import PostRequestWithAccessToken from '../api/axios/requestWithAccessToken';

export default function CommentsSection({ comments, onToggle, postId, isLoading }) {
  const [newCommentText, setNewCommentText] = useState('');

  const accessToken = useSelector(state => state.auth.accessToken);

  const handleWriteComment = async () => {
    if (!newCommentText.trim()) return;
    try {
      let res = await PostRequestWithAccessToken(accessToken, "/comment/" + postId, { content: newCommentText });
      console.log(res.data);
      setNewCommentText(''); // 입력 초기화
      // 필요 시, 댓글 목록 다시 불러오기 또는 상태 업데이트 추가
    } catch (error) {
      console.error("댓글 작성 실패", error);
    }
  };

  return (
    <div
      className="p-4 border rounded bg-gray-50 overflow-y-auto max-h-96 flex flex-col"
      style={{ height: '100%' }}
    >
      <h3 className="text-sm font-semibold mb-2">댓글</h3>

      {/* 댓글 리스트 */}
      <ul className="text-sm text-gray-700 space-y-6 flex-grow overflow-y-auto mb-4 pr-2">
        {comments.map((comment) => (
          <CommentItem key={comment.comment_id} comment={comment} onToggle={onToggle} />
        ))}
      </ul>

      {/* 댓글 작성 UI - 구분선 포함 */}
      <div className="pt-3 border-t border-gray-300">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleWriteComment();
          }}
          className="flex items-center"
        >
          <input
            type="text"
            value={newCommentText}
            onChange={(e) => setNewCommentText(e.target.value)}
            placeholder="댓글을 입력하세요"
            className="flex-grow border rounded-l px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
          <button
            type="submit"
            className={`bg-blue-500 hover:bg-blue-600 text-white rounded-r px-4 py-2 text-sm ${
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

function CommentItem({ comment, onToggle }) {
  const [showReplies, setShowReplies] = useState(false);

  const {
    comment_id,
    author_nickname,
    content,
    created_at,
    replies = [],
    reply_count = 0,
  } = comment;

  function handleToggle() {
    setShowReplies((prev) => {
      const newState = !prev;
      setTimeout(() => {
        if (onToggle) onToggle(comment_id, newState);
      }, 0);
      return newState;
    });
  }

  return (
    <li>
      <div className="flex items-center space-x-2 mb-1 text-gray-800 font-semibold text-base">
        <span>{author_nickname}</span>
        <span className="text-xs text-gray-400">{new Date(created_at * 1000).toLocaleString()}</span>
      </div>
      <div className="mb-2 text-gray-700">{content}</div>

      {reply_count > 0 && (
        <button
          onClick={handleToggle}
          className="text-sm text-blue-500 hover:underline"
          type="button"
        >
          {showReplies ? '답글 숨기기' : `+ 답글 ${reply_count}개 보기`}
        </button>
      )}

      {showReplies && replies.length > 0 && (
        <ul className="ml-6 mt-3 space-y-4 border-l-2 border-gray-300 pl-4">
          {replies.map((reply) => (
            <li key={reply.id}>
              <div className="flex items-center space-x-2 text-gray-800 font-semibold text-base">
                <span>{reply.author}</span>
                <span className="text-xs text-gray-400">{new Date(reply.createdAt * 1000).toLocaleString()}</span>
              </div>
              <div className="text-gray-700">{reply.content}</div>
            </li>
          ))}
        </ul>
      )}
    </li>
  );
}
