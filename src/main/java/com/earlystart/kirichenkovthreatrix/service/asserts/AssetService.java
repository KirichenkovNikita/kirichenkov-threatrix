package com.earlystart.kirichenkovthreatrix.service.asserts;

import com.earlystart.kirichenkovthreatrix.dao.AssertDao;
import com.earlystart.kirichenkovthreatrix.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssertDao assertDao;

    public List<Asset> getAssetsByProjectId(UUID projectId) {
        return assertDao.findByProjectId(projectId);
    }

    public List<Asset> getAssetsByName(String name) {
        return assertDao.findByName(name);
    }

    public List<Asset> getAssetsByMultipleLicenses(List<String> licenses) {
        return licenses.stream()
                .flatMap(license -> assertDao.findByLicensesContains(license).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Asset> getAssetsByMultipleCategories(List<String> categories) {
        return categories.stream()
                .flatMap(category -> assertDao.findByLicenseCategoriesContains(category).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Asset> getAssetsByLicensesAndCategories(List<String> licenses, List<String> categories) {
        List<Asset> assetsByLicenses = getAssetsByMultipleLicenses(licenses);
        Set<UUID> assetsByCategoriesIds = getAssetsByMultipleCategories(categories).stream()
                .map(Asset::getAssetId)
                .collect(Collectors.toSet());
        return assetsByLicenses.stream()
                .filter(asset -> assetsByCategoriesIds.contains(asset.getAssetId()))
                .toList();
    }
}
