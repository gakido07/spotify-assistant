package kara.spotifyassistant.security.auth.apikey;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.SecurityUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ApiKeyRequestFilter extends OncePerRequestFilter {

    private final List<String> publicUrls = Arrays.asList("/auth/**", "/*", "spotify");
    private final AppUserService appUserService;
    private final SecurityUtil securityUtil;

    @Autowired
    public ApiKeyRequestFilter(AppUserService appUserService, SecurityUtil securityUtil) {
        this.appUserService = appUserService;
        this.securityUtil = securityUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return this.publicUrls.stream().anyMatch(url -> new AntPathRequestMatcher(url).matches(request));
    }

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String clientId = securityUtil.extractClientIdFromRoute(request.getRequestURI());
        String apiKey = null;

        try {
             apiKey = request.getHeader("Authorization").substring(7);
        }
        catch (Exception e) {
            logger.error("unable to get api key");
        }

        if(clientId != null && apiKey != null && SecurityContextHolder.getContext().getAuthentication() == null){

            try {
                AppUser appUser = appUserService.findUserById(clientId);
                if (securityUtil.BcryptEncoder().matches(apiKey, appUser.getApiKey())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            clientId,
                            null,
                            appUser.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info(request.getRequestURI() + " accessed by " + appUser.getId());
                }
            }
            catch (Exception exception) {
                logger.debug("Error while authenticating");
                logger.error(exception.getMessage());
            }

        }
        filterChain.doFilter(request, response);
    }
}
