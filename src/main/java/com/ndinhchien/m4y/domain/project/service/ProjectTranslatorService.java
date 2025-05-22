package com.ndinhchien.m4y.domain.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.notification.service.NotificationService;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.AddTranslatorsDto;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.ProjectTranslator;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.project.repository.ProjectTranslatorRepository;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectTranslatorService {

    private final ProjectTranslatorRepository translatorRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Transactional
    public List<ProjectTranslator> adminAddTranslators(User user, AddTranslatorsDto requestDto) {
        Long projectId = requestDto.getProjectId();
        Project project = validateProject(projectId);
        if (!project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
        }
        List<Long> ids = requestDto.getIds();
        List<User> translators = userRepository.findAllById(ids);

        List<ProjectTranslator> added = new ArrayList<>();
        for (User translator : translators) {
            ProjectTranslator projectTranslator = translatorRepository.findByProjectAndUser(project, translator)
                    .orElse(null);
            if (projectTranslator == null) {
                projectTranslator = new ProjectTranslator(project, translator);
            }
            projectTranslator.updateIsAccepted();
            projectTranslator = translatorRepository.save(projectTranslator);
            notificationService.send_ToTranslator_BeingAddedAsTranslator_Noti(project, translator);
            added.add(projectTranslator);
        }
        return added;
    }

    @Transactional
    public ProjectTranslator makeTranslatorRequest(User user, Long projectId) {
        Project project = validateProject(projectId);
        if (translatorRepository.existsByProjectAndUser(project, user)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Please wait for project admin to accept your request.");
        }
        if (project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "You are already a translator of this project.");
        }
        ProjectTranslator projectTranslator = new ProjectTranslator(project, user);
        projectTranslator = translatorRepository.save(projectTranslator);
        notificationService.send_ToAdmin_NewTranslatorRequestComming_Noti(project, user);
        return projectTranslator;
    }

    @Transactional
    public ProjectTranslator adminAcceptTranslatorRequest(User user, Long requestId) {
        ProjectTranslator projectTranslator = validateProjectTranslator(requestId);
        Project project = projectTranslator.getProject();
        if (!project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
        }
        if (projectTranslator.getIsAccepted() == false) {
            projectTranslator.updateIsAccepted();
            projectTranslator = translatorRepository.save(projectTranslator);

            notificationService.send_ToTranslator_TranslatorRequestAccepted_Notif(project, projectTranslator.getUser());
        }
        return projectTranslator;
    }

    @Transactional(readOnly = true)
    public boolean isTranslator(Project project, User user) {
        if (project.isAdmin(user)) {
            return true;
        }
        ProjectTranslator projectTranslator = translatorRepository.findByProjectAndUser(project, user).orElse(null);
        if (projectTranslator != null && projectTranslator.getIsAccepted()) {
            return true;
        }
        return false;
    }

    private Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
        });
    }

    private ProjectTranslator validateProjectTranslator(Long requestId) {
        return translatorRepository.findById(requestId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.REQUEST_NOT_FOUND);
        });
    }

}
