  import React, { useState, useEffect, useRef, useCallback } from 'react';
  import PostCard from './PostCard';
  import { useDispatch, useSelector } from 'react-redux';
  import { showModal } from '../redux/modalStackSlice';
  import { DeleteRequestWithAccessToekn, DeleteRequestWithAccessTokenWithBody } from '../api/axios/requestWithAccessToken';

  const API_URL = 'http://localhost:8080/dumbo-backend/post';

  export default function ArticleTest() {
    const [posts, setPosts] = useState([]);
    const [cursor, setCursor] = useState(null); // { createdAt, postId }
    const [hasMore, setHasMore] = useState(true);
    const [isLoading, setIsLoading] = useState(false);

    const loadMoreRef = useRef(null);

    const dispatch = useDispatch();

    const fetchPosts = useCallback(async () => {
      if (isLoading || !hasMore) return;

      setIsLoading(true);

      try {
        const params = new URLSearchParams();
        if (cursor?.createdAt) params.append('createdAtCursor', cursor.createdAt);
        if (cursor?.postId) params.append('postIdCursor', cursor.postId);
        // 최신 글부터 보기이므로 reverse는 false 또는 생략

        const res = await fetch(`${API_URL}?${params.toString()}`);
        if (!res.ok) throw new Error('Failed to fetch posts');

        const json = await res.json();

        setPosts((prev) => [...prev, ...json.data]);

        setHasMore(json.hasMore);
        if (json.hasMore) {
          setCursor({
            createdAt: json.nextCreatedAt,
            postId: json.id,
          });
        }
      } catch (error) {
        console.error('게시글 로딩 오류:', error);
      } finally {
        setIsLoading(false);
      }
    }, [cursor, hasMore, isLoading]);

    // 최초 로드
    useEffect(() => {
      fetchPosts();
    }, []);

    // IntersectionObserver로 무한 스크롤 트리거
    useEffect(() => {
      if (!loadMoreRef.current) return;

      const observer = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting && hasMore && !isLoading) {
            fetchPosts();
          }
        },
        { rootMargin: '200px' }
      );

      observer.observe(loadMoreRef.current);

      return () => {
        if (loadMoreRef.current) observer.unobserve(loadMoreRef.current);
      };
    }, [fetchPosts, hasMore, isLoading]);

      const modalStack = useSelector(state => state.modal.modalStack);
      useEffect(() => {
      if (modalStack.length > 0) {
        // 모달 열림 → 스크롤 막기
        document.body.style.overflow = 'hidden';
      } else {
        // 모달 닫힘 → 스크롤 원복
        document.body.style.overflow = '';
      }

      // Cleanup: 컴포넌트 언마운트 시 원복
      return () => {
        document.body.style.overflow = '';
      };
    }, [modalStack.length]);
      
    const isLoggedIn = useSelector(state => state.auth.accessToken);

  async function resign()
  {
    let res = await DeleteRequestWithAccessTokenWithBody(isLoggedIn, "/auth/resign", { password:"Ss07241129!"});
    console.log(res.data);
    
  }

    return (
      <div className="max-w-3xl mx-auto mt-10 px-4">
        {posts.map((post) => (
          <PostCard key={post.post_id} post={post} />
        ))}
        <div ref={loadMoreRef} style={{ height: 1 }} />

        {isLoading && (
          <div className="flex justify-center py-6">
            <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-indigo-500" />
          </div>
        )}

        {!hasMore && (
          <div className="text-center text-gray-400 py-4">
            모든 게시글을 불러왔습니다.
          </div>
        )}
        {/* 글쓰기 버튼 - 고정 */}
        {isLoggedIn &&
        <button
          onClick={() => { dispatch(showModal('draft')); }}
          className="
            fixed
            left-8
            bottom-8
            px-4
            py-4
          bg-indigo-500
          text-2xl
          rounded-full
          shadow-lg
          cursor-pointer
          hover:bg-indigo-700">
            🪶
          </button>
        }
        <button
          onClick={() => { resign(); }}
          className="
            fixed
            left-100
            bottom-8
            px-10
            py-4
          bg-indigo-500
          text-2xl
          rounded-full
          shadow-lg
          cursor-pointer
          hover:bg-indigo-700">
            회원탈퇴테스트
          </button>
      </div>
    );
  }
