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
                     src_url as srcUrl,
                     channel_id as channelId,
                     title,
                     description,
                     duration,
                     src_lang_code as srcLangCode,
                     des_lang_code as desLangCode,
                     view_count as viewCount,
                     react_count as reactCount,
                     admin_id as adminId,
                     created_at as createdAt,
                     updated_at as updatedAt,
                     ts_rank(full_text, to_tsquery('simple', :keywords)) AS relevance
            FROM     projects
            WHERE    full_text @@ to_tsquery('simple', :keywords)
            """;

    @Query(value = SEARCH_QUERY, nativeQuery = true)
    Page<IProjectSearch> search(String keywords, Pageable pageable);

    @Query(value = SEARCH_QUERY + " AND des_lang_code = :desLangCode", nativeQuery = true)
    Page<IProjectSearch> search(String keywords, String desLangCode, Pageable pageable);

    boolean existsByIdAndAdminId(Long id, Long userId);

    boolean existsBySrcUrlAndDesLangCode(String srcUrl, String desLangCode);

    long countBySrcUrl(String srcUrl);

    Optional<Project> findBySrcUrlAndAdmin(String srcUrl, User admin);

    Optional<Project> findBySrcUrlAndDesLangCode(String srcUrl, String desLangCode);

    Optional<IProject> findProjectById(Long id);

    Optional<IBasicProject> findOneById(Long id);

    List<Project> findBySrcUrl(String srcUrl);

    Page<IBasicProject> findAllBy(Pageable pageable);

    List<IBasicProject> findAllByChannel(Channel channel);

    Page<IBasicProject> findAllByDesLangCode(String desLangCode, Pageable pageable);

}
