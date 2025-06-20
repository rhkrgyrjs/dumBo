package com.dumbo.repository.db;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dumbo.repository.dao.CommentDao;
import com.dumbo.repository.dto.CommentDTO;
import com.dumbo.repository.dto.CursorResult;
import com.dumbo.repository.entity.Comment;

import com.dumbo.repository.entity.User;

@Repository
public class CommentDaoJdbcService implements CommentDao
{
    @Autowired
    private DBConnectionMaker connectionMaker;

    public boolean createComment(User user, String postId, String content)
    {
        String sql = "INSERT INTO comments (post_id, author_id, author_nickname, content) VALUES (?, ?, ?, ?)";
        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, postId);
            ps.setString(2, user.getId());
            ps.setString(3, user.getNickname());
            ps.setString(4, content);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public CursorResult<CommentDTO> getCommentFeed(String postId, Long createdAtCursor, String commentIdCursor, int limit, boolean reverse) throws SQLException
    {
        String sql = "SELECT id, author_id, author_nickname, content, created_at, updated_at, reply_count FROM comments WHERE post_id = ?";
        boolean hasCursor = createdAtCursor != null && commentIdCursor != null;

        // 커서 처리
        if (hasCursor) 
        {
            if (reverse) sql += " AND (created_at > ? OR (created_at = ? AND id > ?)) ";
            else sql += " AND (created_at < ? OR (created_at = ? AND id < ?)) ";
        }

        // 정렬 처리 : 작성 시각이 겹치면 userId의 오름차순 정렬
        sql += " ORDER BY created_at " + (reverse ? "ASC" : "DESC") + ", id ASC LIMIT ?";

        try (Connection c = connectionMaker.makeConnection(); PreparedStatement ps = c.prepareStatement(sql)) 
        {
            int idx = 1;
            ps.setString(idx++, postId);

            if (hasCursor) 
            {
                Timestamp ts = new Timestamp(createdAtCursor * 1000); // 초 -> 밀리초 변환
                ps.setTimestamp(idx++, ts);
                ps.setTimestamp(idx++, ts);
                ps.setString(idx++, commentIdCursor);
            }

            ps.setInt(idx, limit + 1); // hasmore 추출 위해

            try (ResultSet rs = ps.executeQuery()) 
            {
                List<CommentDTO> comments = new ArrayList<>();

                while (rs.next()) 
                {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(rs.getString("id"));
                    dto.setAuthorId(rs.getString("author_id"));
                    dto.setAuthorNickname(rs.getString("author_nickname"));
                    dto.setContent(rs.getString("content"));
                    Timestamp created = rs.getTimestamp("created_at");
                    dto.setCreatedAt(created != null ? created.getTime()/1000 : null); // 밀리초 -> 초
                    Timestamp updated = rs.getTimestamp("updated_at");
                    dto.setUpdatedAt(updated != null ? updated.getTime()/1000 : null); // 밀리초 -> 초
                    dto.setReplyCount(rs.getInt("reply_count"));
                    comments.add(dto);
                }

                boolean hasMore = comments.size() > limit;
                if (hasMore) comments.remove(comments.size() - 1); // hasmore 판별 후 제거

                Long nextCreatedAt = null;
                String nextCommentId = null;
                if (!comments.isEmpty()) 
                {
                    CommentDTO last = comments.get(comments.size() - 1);
                    nextCreatedAt = last.getCreatedAt();
                    nextCommentId = last.getId();
                }
                return new CursorResult<CommentDTO>(comments, nextCreatedAt, nextCommentId, hasMore);
            }
        }
    }

}
