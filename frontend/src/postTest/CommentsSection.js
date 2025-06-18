import React, { useState } from 'react';
import { useSelector } from "react-redux";
import PostRequestWithAccessToken from '../api/axios/requestWithAccessToken';

export default function CommentsSection({ comments, onToggle, postId, isLoading }) {
  const [newCommentText, setNewCommentText] = useState('');

  const accessToken = useSelector(state => state.auth.accessToken);

  const handleWriteComment = async () => 
  {
    let res = await PostRequestWithAccessToken(accessToken, "/comment/"+postId, { content: newCommentText });
    console.log(res.data);
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
          <CommentItem key={comment.id} comment={comment} onToggle={onToggle} />
        ))}
      </ul>

      {/* 댓글 작성 UI - 구분선 포함 */}
      <div className="pt-3 border-t border-gray-300">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleWriteComment();
            // 현재는 제출 기능 없음
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
            className="bg-blue-500 hover:bg-blue-600 text-white rounded-r px-4 py-2 text-sm cursor-not-allowed"
            
          >
            쓰기
          </button>
        </form>
      </div>
    </div>
  );
}

function CommentItem({ comment, onToggle }) {
  const [showReplies, setShowReplies] = React.useState(false);
  const { author, content, createdAt, replies = [] } = comment;

  function handleToggle() {
    setShowReplies((prev) => {
      const newState = !prev;
      setTimeout(() => {
        if (onToggle) onToggle(comment.id, newState);
      }, 0);
      return newState;
    });
  }

  return (
    <li>
      <div className="flex items-center space-x-2 mb-1 text-gray-800 font-semibold text-base">
        <span>{author}</span>
        <span className="text-xs text-gray-400">{new Date(createdAt).toLocaleString()}</span>
      </div>
      <div className="mb-2 text-gray-700">{content}</div>

      {replies.length > 0 && (
        <button
          onClick={handleToggle}
          className="text-sm text-blue-500 hover:underline"
          type="button"
        >
          {showReplies ? '답글 숨기기' : `+ 답글 ${replies.length}개 보기`}
        </button>
      )}

      {showReplies && replies.length > 0 && (
        <ul className="ml-6 mt-3 space-y-4 border-l-2 border-gray-300 pl-4">
          {replies.map((reply) => (
            <li key={reply.id}>
              <div className="flex items-center space-x-2 text-gray-800 font-semibold text-base">
                <span>{reply.author}</span>
                <span className="text-xs text-gray-400">{new Date(reply.createdAt).toLocaleString()}</span>
              </div>
              <div className="text-gray-700">{reply.content}</div>
            </li>
          ))}
        </ul>
      )}
    </li>
  );
}
