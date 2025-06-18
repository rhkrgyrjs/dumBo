// components/VoteBar.js
import React from 'react';

export default function VoteBar({ likes, dislikes, className = '' }) {
  const total = likes + dislikes;
  const likeRatio = total === 0 ? 0.5 : likes / total;
  const dislikeRatio = 1 - likeRatio;

  const likePercent = Math.round(likeRatio * 100);
  const dislikePercent = 100 - likePercent;

  return (
    <div
      className={`h-3 rounded-full overflow-hidden bg-gray-200 flex text-[10px] font-medium ${className}`}
      title={`좋아요 ${likes}개, 싫어요 ${dislikes}개`}
    >
      <div
        className="bg-green-500 text-white text-right pr-1 flex items-center justify-end"
        style={{ width: `${likePercent}%` }}
      >
        {likes > 0 && <span>{likePercent}%</span>}
      </div>
      <div
        className="bg-red-500 text-white text-left pl-1 flex items-center justify-start"
        style={{ width: `${dislikePercent}%` }}
      >
        {dislikes > 0 && <span>{dislikePercent}%</span>}
      </div>
    </div>
  );
}
