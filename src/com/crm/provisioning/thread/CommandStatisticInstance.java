package com.crm.provisioning.thread;

import com.crm.kernel.message.Constants;
import com.crm.kernel.queue.QueueFactory;
import com.crm.provisioning.message.CommandMessage;
import com.crm.thread.DispatcherInstance;

public class CommandStatisticInstance extends DispatcherInstance {

	public CommandStatisticInstance() throws Exception {
		super();
	}

	@Override
	public void doProcessSession() throws Exception {
		while (isAvailable()) {
			CommandMessage request = null;
			request = QueueFactory.detachStatistic();

			if (request == null) {
				break;
			}

			try {
				((CommandStatisticThread) dispatcher).putStatisticMap(
						request.getProductId(),
						request.getStatus() == Constants.ORDER_STATUS_APPROVED);
			} catch (Exception e) {
				throw e;
			}
		}
	}
}
