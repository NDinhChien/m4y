package com.ndinhchien.m4y.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IProjectSearch;
import com.ndinhchien.m4y.domain.project.entity.Channel;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    static final String SEARCH_QUERY = """
            SELECT   id,
                     channel_id as channelId,
                     channel_url as channelUrl,
                     video_id as videoId,
                     video_url as videoUrl,
                     name,
                     description,
                     duration,
                     lang_code as langCode,
                     view_count as viewCount,
                     react_count as reactCount,
                     admin_id as adminId,
                     is_completed as isCompleted,
                     created_at as createdAt,
                     updated_at as updatedAt,
                     ts_rank(full_text, to_tsquery('simple', :keywords)) AS relevance
            FROM     projects
            WHERE    full_text @@ to_tsquery('simple', :keywords)
            """;

    @Query(value = SEARCH_QUERY, nativeQuery = true)
    Page<IProjectSearch> search(String keywords, Pageable pageable);

    @Query(value = SEARCH_QUERY + " AND lang_code = :langCode", nativeQuery = true)
    Page<IProjectSearch> search(String keywords, String langCode, Pageable pageable);

    long countByVideoUrl(String videoUrl);

    boolean existsByIdAndAdminId(Long id, Long userId);

    boolean existsByVideoUrlAndLangCode(String videoUrl, String langCode);

    boolean existsByVideoUrlAndAdmin(String videoUrl, User admin);

    Optional<Project> findByVideoUrlAndAdmin(String videoUrl, User admin);

    Optional<Project> findByVideoUrlAndLangCode(String videoUrl, String langCode);

    Optional<IProject> findProjectById(Long id);

    Optional<IBasicProject> findOneById(Long id);

    List<Project> findByVideoUrl(String videoUrl);

    Page<IBasicProject> findAllBy(Pageable pageable);

    List<IBasicProject> findAllByChannel(Channel channel);

    Page<IBasicProject> findAllByLangCode(String langCode, Pageable pageable);

}
