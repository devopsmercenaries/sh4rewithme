package me.sh4rewith.web.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

@Component
public class AppInfoExpositionInterceptor implements HandlerInterceptor {
	@Autowired
	@Qualifier("appVersion")
	private String appVersion;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		boolean isRedirectView = modelAndView.getView() instanceof RedirectView;
		boolean isViewObject = modelAndView.getView() == null;
		// if the view name is null then set a default value of true
		boolean viewNameStartsWithRedirect = (modelAndView.getViewName() == null ? true : modelAndView.getViewName().startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX));

		if (modelAndView.hasView() && (
				(isViewObject && !isRedirectView) ||
				(!isViewObject && !viewNameStartsWithRedirect))) {
			addCommonModelData(modelAndView);
		}
	}

	private void addCommonModelData(ModelAndView modelAndView) {
		modelAndView.addObject("appVersion", appVersion);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
