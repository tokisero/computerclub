package com.tokiserskyy.computerclub.aspects;

import com.tokiserskyy.computerclub.service.VisitCounterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class VisitFilter extends OncePerRequestFilter {
    private final VisitCounterService visitCounterService;

    public VisitFilter(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        visitCounterService.incrementVisit(url);
        filterChain.doFilter(request, response);
    }
}