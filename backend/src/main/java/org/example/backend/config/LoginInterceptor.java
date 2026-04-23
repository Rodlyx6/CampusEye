package org.example.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.backend.common.Result;
import org.example.backend.entity.SysUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "用户未登录")));
            return false;
        }

        String uri = request.getRequestURI();
        boolean isAdminApi = uri.contains("/building/add")
                || uri.contains("/building/update")
                || uri.contains("/building/delete")
                || uri.contains("/building-image/add")
                || uri.contains("/building-image/batchAdd")
                || uri.contains("/building-image/replace")
                || uri.contains("/building-image/update")
                || uri.contains("/building-image/delete")
                || uri.contains("/file/upload")
                || uri.contains("/file/uploadBatch");

        if (isAdminApi) {
            SysUser user = (SysUser) session.getAttribute("currentUser");
            if (!"admin".equals(user.getRole())) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(200);
                response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "无权限操作")));
                return false;
            }
        }

        return true;
    }
}
