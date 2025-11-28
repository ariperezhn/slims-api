/*
 * Copyright 2021 Agilent Technologies Inc.
 */

package com.genohm.slims.custom.beans;

import com.genohm.slims.api.ConvertRecordService;
import com.genohm.slims.common.model.Content;
import com.genohm.slims.common.model.ContentMeta;
import com.genohm.slims.common.model.Slims;
import com.genohm.slims.common.slimsgate.SlimsgateParameter;
import com.genohm.slims.common.util.SetUtil;
import com.genohm.slims.server.dao.common.Dao;
import com.genohm.slims.server.dao.criterion.SlimsRestrictions;
import com.genohm.slims.server.quantity.Quantity;
import com.genohm.slims.server.repository.queriers.ContentRecordQueries;
import com.genohm.slims.server.service.PublicApi;
import com.genohm.slims.server.service.unit.UnitService;
import com.genohm.slims.server.util.MapUtil;
import com.genohm.slimsgate.camel.gatekeeper.SlimsGateErrorException;
import com.genohm.slimsgateclient.workflow.SlimsFlowInitParam;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.genohm.slims.custom.RequestFinishedConfiguration;
import com.genohm.slimsgate.camel.gatekeeper.SlimsGateKeeperConstants;
import com.genohm.slimsgate.camel.gatekeeper.SlimsProxy;

import java.util.*;

@Component
public class SayHello {

	@Autowired
	private RequestFinishedConfiguration requestFinishedConfiguration;

    @Autowired
    private Dao<Content> contentDao;

    @Autowired
    private ConvertRecordService convertRecordService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ContentRecordQueries contentRecordQueries;

	@Transactional
	@Handler
	public void sayHi(@Header(SlimsGateKeeperConstants.SLIMS_PROXY) SlimsProxy slimsProxy, @Header(SlimsGateKeeperConstants.SLIMS_WORKFLOW_INIT_PARAMETER) SlimsFlowInitParam slimsFlowInitParam) //throws InterruptedException {
    {
        // from ("direct:hello")
        //What if I want to have a log but also get the body or header?
        //what if I want to do this: 	public void updateSomeContent(@Header(SlimsGateKeeperConstants.SLIMS_WORKFLOW_INIT_PARAMETER) SlimsFlowInitParam slimsFlowInitParam) {
        //so I can do Set<Long> selectedContentPks = SetUtil.getAsLongSet(slimsFlowInitParam.getInputParameterValues().get(SlimsgateParameter.SLIMS_SELECT_SAMPLES));

		Logger log = slimsProxy.getLogger(getClass());

        Set<Long> selectedContentPks = SetUtil.getAsLongSet(slimsFlowInitParam.getInputParameterValues().get(SlimsgateParameter.SLIMS_SELECT_SAMPLES));
        List<Map<String, Object>> contentsToUpdate = contentRecordQueries.fetchIn(ContentMeta.PK, selectedContentPks); //fetchIn(ContentMeta.PK,selectedContentPks);


        //log.info((String) contentsToUpdate.getFirst().get("cntn_cf_fk_belongsToSample"));
        Set<?> originalSamplePks = MapUtil.collectProperty(contentsToUpdate, requestFinishedConfiguration.getBelongsToSample());
        log.info(originalSamplePks.toString());

        List<Map<String, Object>> copies = new ArrayList<>();

        for (Map<String, Object> obj : contentsToUpdate) {
            Map <String, Object> copy = new HashMap<>(obj);
            copy.remove(ContentMeta.PK);
            copy.remove(ContentMeta.CREATED_BY);
            copy.remove(ContentMeta.CREATED_ON);
            copy.remove(ContentMeta.MODIFIED_BY);
            copy.remove(ContentMeta.MODIFIED_ON);
            copy.remove(ContentMeta.BAR_CODE);
            copy.remove(ContentMeta.ID);
            Map<String, Object> c = contentDao.add(copy);
            log.info(c.get(ContentMeta.BAR_CODE).toString());

            c.put("cntn_cf_mass", new Quantity(5, unitService.convertToUnit("g")));
            Map<String, Object> cUpdated = contentDao.update(c);

            log.info("Mass updated to %s".formatted(cUpdated.get("cntn_cf_mass").toString()));
            copies.add(cUpdated);

            contentDao.remove(obj);

            log.info("removed original %s".formatted(obj.get(ContentMeta.BAR_CODE)));
            //copies.add(contentDao.add(copy));


        }

        log.info("Created copies %d".formatted(copies.size()));



        //cntn_cf_fk_belongsToSample

        /*
        log.info("Hello from the slimsgate template plugin, this is my configuration: \n" +
				"parameterOne: " + requestFinishedConfiguration.getParameterOne() + "\n" +
				"parameterTwo: " + requestFinishedConfiguration.getParameterTwo());
        log.error("This is an error test");
           */
        //Thread.sleep(20000);

            /*
        for (int i = 0; i < 20; i++) {
            slimsProxy.checkForInterruption();
            Thread.sleep(1000);
            slimsProxy.setProgress(i, 20, "%s completed".formatted(i));

        }
        throw new SlimsGateErrorException("This is a test error");

             */
	}
	
}
