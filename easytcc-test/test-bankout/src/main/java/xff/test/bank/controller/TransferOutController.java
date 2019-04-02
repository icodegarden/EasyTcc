package xff.test.bank.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import xff.test.bank.service.TransferOutService;
import xff.test.dto.TransferAmtDto;
/**
 * 
 * @author Fangfang.Xu
 */
@Controller
public class TransferOutController {

	@Autowired
	TransferOutService transferService;

	@RequestMapping(value = "/transferout")
	public String transferout() {
		TransferAmtDto dto = new TransferAmtDto();
		dto.setAmt(100L);
		dto.setTransferNumber(UUID.randomUUID().toString());
		transferService.transferAmt(dto);

		return "redirect:/";
	}

	@RequestMapping(value = "/test-transferout", method = RequestMethod.POST)
	public @ResponseBody String testTransferout(@RequestBody TransferAmtDto dto) {
		transferService.transferAmt(dto);

		return "";
	}
}
