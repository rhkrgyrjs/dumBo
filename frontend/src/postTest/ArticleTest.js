import React, { useState, useEffect, useRef, useCallback } from 'react';
import PostCard from './PostCard';

const PAGE_SIZE = 20;
const MAX_POSTS = 40;

export default function ArticleTest() {
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(0);
  const loadMoreRef = useRef();
  const isLoadingRef = useRef(false);

  const fetchPosts = useCallback(async (pageNum) => {
    isLoadingRef.current = true;

    // 여기에 실제 API 호출 로직 넣기
    // 예: const res = await fetch(`/api/posts?offset=${pageNum * PAGE_SIZE}&limit=${PAGE_SIZE}`);
    // const newPosts = await res.json();

    // 임시 더미 데이터 생성 (실제 API로 바꾸면 됨)
    const newPosts = Array(PAGE_SIZE).fill(null).map((_, i) => {
      const id = pageNum * PAGE_SIZE + i + 1;
      return {
        id: `post_${id}`,
        nickname: `홍길동_${id}`,
        title: `게시글 제목 ${id}`,
        contentHtml: `<p>게시글 내용 예시 ${id}.</p>`,
        thumbnailImage: 'https://via.placeholder.com/600x300',
        comments: [],
      };
    });

    setPosts((prev) => {
      const combined = [...prev, ...newPosts];
      if (combined.length > MAX_POSTS) {
        // 오래된 게시글 10개 삭제 (가장 앞 10개)
        return combined.slice(combined.length - MAX_POSTS);
      }
      return combined;
    });

    isLoadingRef.current = false;
  }, []);

  // 무한 스크롤 감지
  useEffect(() => {
    if (!loadMoreRef.current) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !isLoadingRef.current) {
          setPage((prev) => prev + 1);
        }
      },
      { rootMargin: '200px' }
    );

    observer.observe(loadMoreRef.current);

    return () => {
      if (loadMoreRef.current) observer.unobserve(loadMoreRef.current);
    };
  }, []);

  // page가 바뀔 때마다 게시글 fetch
  useEffect(() => {
    fetchPosts(page);
  }, [page, fetchPosts]);

  // 초기 데이터 로드
  useEffect(() => {
    setPage(0);
  }, []);

  return (
    <div className="max-w-xl mx-auto mt-10 px-4">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
      <div ref={loadMoreRef} style={{ height: 1 }} />
    </div>
  );
}
