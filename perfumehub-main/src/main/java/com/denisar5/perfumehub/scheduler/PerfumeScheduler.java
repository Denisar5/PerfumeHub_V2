package com.denisar5.perfumehub.scheduler;

import com.denisar5.perfumehub.entity.Perfume;
import com.denisar5.perfumehub.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerfumeScheduler {

    private final PerfumeRepository perfumeRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @CacheEvict(
            value = "perfumeBrands",
            allEntries = true
    )
    @Transactional
    public void hideOutOfStockPerfumes()  {

        List<Perfume> perfumes =
                perfumeRepository.findAll();

        int changed = 0;

        for (Perfume perfume : perfumes) {

            if (perfume.getStockQuantity() <= 0
                    && perfume.isVisible()) {

                perfume.setVisible(false);
                changed++;
            }
        }

        if (changed > 0) {
            log.info(
                    "Scheduled task hid {} out-of-stock perfumes",
                    changed
            );
        }
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional(readOnly = true)
    public void logCatalogStatistics() {

        long totalPerfumes =
                perfumeRepository.count();

        long visiblePerfumes =
                perfumeRepository.countByVisibleTrue();

        log.info(
                "Catalog statistics: total={}, visible={}",
                totalPerfumes,
                visiblePerfumes
        );
    }
}