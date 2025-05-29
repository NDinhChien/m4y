package com.ndinhchien.m4y.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IBasicProjectWithRequest;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IProject;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IProjectSearch;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.user.entity.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    static final String SEARCH_QUERY = """
            SELECT   id,
                     channel_id as channelId,
                     channel_url as channelUrl,
                     channel_image as channelImage,
                     video_id as videoId,
                     video_url as videoUrl,
                     video_image as videoImage,
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

    boolean existsByIdAndAdmin(Long id, User admin);

    boolean existsByVideoUrlAndLangCode(String videoUrl, String langCode);

    boolean existsByVideoUrlAndAdmin(String videoUrl, User admin);

    Optional<Project> findByVideoUrlAndAdmin(String videoUrl, User admin);

    Optional<Project> findByVideoUrlAndLangCode(String videoUrl, String langCode);

    Optional<IProject> findProjectById(Long id);

    Optional<IBasicProject> findOneById(Long id);

    List<Project> findByVideoUrl(String videoUrl);

    Page<IBasicProject> findAllBy(Pageable pageable);

    List<IBasicProjectWithRequest> findAllByAdmin(User admin);

    List<IBasicProject> findAllByIdIn(List<Long> ids);

    List<IBasicProject> findAllByChannelUrl(String channelUrl);

    List<IBasicProject> findAllByVideoUrl(String videoUrl);

    Page<IBasicProject> findAllByChannelUrl(String channelUrl, Pageable pageable);

    Page<IBasicProject> findAllByLangCode(String langCode, Pageable pageable);

}
