package com.ndinhchien.m4y.domain.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ndinhchien.m4y.domain.notification.service.NotificationService;
import com.ndinhchien.m4y.domain.project.dto.ProjectRequestDto.HandleTranslatorRequestDto;
import com.ndinhchien.m4y.domain.project.dto.ProjectResponseDto.IRequest;
import com.ndinhchien.m4y.domain.project.entity.Project;
import com.ndinhchien.m4y.domain.project.entity.Request;
import com.ndinhchien.m4y.domain.project.repository.ProjectRepository;
import com.ndinhchien.m4y.domain.project.repository.RequestRepository;
import com.ndinhchien.m4y.domain.project.type.RequestStatus;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.global.exception.BusinessException;
import com.ndinhchien.m4y.global.exception.ErrorMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<IRequest> getUserRequests(User user) {
        return requestRepository.findAllByUser(user);
    }

    @Transactional
    public Request makeTranslatorRequest(User user, Long projectId) {
        Project project = validateProject(projectId);
        if (requestRepository.existsByProjectAndUser(project, user)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Please wait for project admin to accept your request.");
        }
        if (project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "You are already a translator of this project.");
        }
        Request request = new Request(project, user);
        request = requestRepository.save(request);
        notificationService.sendToAdminNewRequest(project, user);
        return request;
    }

    @Transactional
    public int hardDeleteRequest(User user, Long requestId) {
        Request request = validateRequest(requestId);
        if (!request.isOwner(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "You are not owner of this request");
        }
        if (request.isPending()) {
            requestRepository.delete(request);
            return 1;
        }
        return 0;
    }

    @Transactional
    public Request adminHandleRequest(User user, HandleTranslatorRequestDto requestDto) {
        Long requestId = requestDto.getId();
        RequestStatus requestStatus = requestDto.getStatus();
        Request request = validateRequest(requestId);
        Project project = request.getProject();
        if (!project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
        }

        if (request.isPending() && !requestStatus.value.equals(RequestStatus.PENDING.value)) {
            request.updateStatus(requestStatus);
            request = requestRepository.save(request);
            notificationService.sendToUserRequestHandled(project, request.getUser());
        }
        return request;
    }

    @Transactional
    public List<Request> adminAddTranslators(User user, Long projectId, List<Long> ids) {
        Project project = validateProject(projectId);
        if (!project.isAdmin(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorMessage.NOT_PROJECT_ADMIN);
        }
        List<User> translators = userRepository.findAllById(ids);

        List<Request> added = new ArrayList<>();
        for (User translator : translators) {
            Request request = requestRepository.findByProjectAndUser(project, translator)
                    .orElse(null);
            if (request == null) {
                request = new Request(project, translator);
            }
            request.updateStatus(RequestStatus.ACCEPTED);
            request = requestRepository.save(request);
            notificationService.sendToUserTranslatorAdded(project, translator);
            added.add(request);
        }
        return added;
    }

    @Transactional(readOnly = true)
    public boolean isTranslator(Project project, User user) {
        if (project.isAdmin(user)) {
            return true;
        }
        Request request = requestRepository.findByProjectAndUser(project, user).orElse(null);
        if (request != null && request.isAccepted()) {
            return true;
        }
        return false;
    }

    private Project validateProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.PROJECT_NOT_FOUND);
        });
    }

    private Request validateRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorMessage.REQUEST_NOT_FOUND);
        });
    }

}
