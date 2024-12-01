package com.ecom.service.impl;

import com.ecom.service.CommonService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public void removeSessionMessage() {
        HttpSession session = ((ServletRequestAttributes)(Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest().getSession();
        session.removeAttribute("errorMsg");
        session.removeAttribute("succMsg");
//        session.removeAttribute("msg");
//        session.invalidate();
    }
}
