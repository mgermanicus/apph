package com.viseo.apph.service;

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

    public SettingResponse getSettings() {
        return new SettingResponse(settingDao.getSetting());
    }

    @Transactional
    public SettingResponse updateSettings(SettingRequest request) {
        Setting setting = settingDao.getSetting();
        setting.setDownloadSize(request.getDownloadSize());
        setting.setUploadSize(request.getUploadSize());
        return new SettingResponse().setMessage("OK");
    }
}
