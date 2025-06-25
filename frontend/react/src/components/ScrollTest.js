import React, { useState, useRef, useEffect, useCallback } from 'react';
import { VariableSizeList as List } from 'react-window';
import PostCard from '../postTest/PostCard';

// 예시 데이터 생성 (10,000개)
const generatePosts = (count) =>
  Array.from({ length: count }, (_, i) => ({
    id: i,
    nickname: `유저${i}`,
    title: `게시글 제목 ${i}`,
    createdAt: Date.now() - i * 1000000,
    updatedAt: Date.now() - i * 900000,
    contentHtml:
      '<p>이것은 게시글 ' +
      i +
      '의 내용입니다. 더보기 펼침 여부에 따라 높이가 달라집니다.</p>',
    thumbnailImage: i % 3 === 0 ? 'https://picsum.photos/seed/' + i + '/300/150' : null,
    comments: Array(i % 5)
      .fill(0)
      .map((_, j) => ({ id: j, text: `댓글 ${j} 입니다.` })),
  }));

const posts = generatePosts(10000);

export default function ScrollTest() {
  const listRef = useRef();

  // index별 높이 상태 (초기 기본값은 200)
  const [heights, setHeights] = useState({});

  // PostCard에서 높이 변경 통보 받기
  const setItemHeight = useCallback((index, size) => {
    setHeights((prev) => {
      if (prev[index] === size) return prev;
      const newHeights = { ...prev, [index]: size };
      // 높이 변경 시 리스트에 알려줌
      listRef.current?.resetAfterIndex(index);
      return newHeights;
    });
  }, []);

  // 각 아이템 높이 반환, 없으면 기본 200
  const getItemSize = (index) => heights[index] || 200;

  const Row = ({ index, style }) => {
    return (
      <div style={style}>
        <PostCard
          post={posts[index]}
          onHeightChange={(height) => setItemHeight(index, height)}
        />
      </div>
    );
  };

  return (
    <List
      ref={listRef}
      height={600}
      width="100%"
      itemCount={posts.length}
      itemSize={getItemSize}
      overscanCount={5}
    >
      {Row}
    </List>
  );
}
