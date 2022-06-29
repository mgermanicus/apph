package com.viseo.apph.service;

import com.viseo.apph.config.multipartConfig.UpdatableMultipartConfigElement;
import com.viseo.apph.dao.SettingDao;
import com.viseo.apph.domain.Setting;
import com.viseo.apph.dto.SettingRequest;
import com.viseo.apph.dto.SettingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SettingService {
    @Autowired
    SettingDao settingDao;

    @Autowired
    UpdatableMultipartConfigElement updatableMultipartConfigElement;

    public SettingResponse getSettings() {
        return new SettingResponse(settingDao.getSetting());
    }

    @Transactional
    public SettingResponse updateSettings(SettingRequest request) {
        Setting setting = settingDao.getSetting();
        setting.setDownloadSize(request.getDownloadSize());
        setting.setUploadSize(request.getUploadSize());
        updatableMultipartConfigElement.setMaxFileSize(request.getUploadSize() * 1048576).setMaxRequestSize(request.getUploadSize() * 1048576);
        return new SettingResponse().setMessage("OK");
    }
}
