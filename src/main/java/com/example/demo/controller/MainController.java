package com.example.demo.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.dao.BankAccountDAO;
import com.example.demo.exception.BankTransactionException;
import com.example.demo.form.SendMoneyForm;
import com.example.demo.model.BankAccountInfo;
 
@Controller
public class MainController {
 
	/**
	 * dùng Spring IoC để khởi tạo biến này
	 */
    @Autowired
    private BankAccountDAO bankAccountDAO;    //Spring IoC (or DI)
 
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showBankAccounts(Model model) {
        List<BankAccountInfo> list = bankAccountDAO.getBankAccounts();
 
        model.addAttribute("accountInfos", list);  //add Model to View  (MVC model)
 
        return "accountsPage";
    }
 
    @RequestMapping(value = "/sendMoney", method = RequestMethod.GET)
    public String viewSendMoneyPage(Model model) {
 
        SendMoneyForm form = new SendMoneyForm(1L, 2L, 700d);  // Model contains the old values
 
        model.addAttribute("sendMoneyForm", form);  //add Model to View  (MVC model)
 
        return "sendMoneyPage";
    }
 
    @RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
    public String processSendMoney(Model model, SendMoneyForm sendMoneyForm) {
 
        System.out.println("Send Money::" + sendMoneyForm.getAmount());
 
        try {
            bankAccountDAO.sendMoney(sendMoneyForm.getFromAccountId(), //
                    sendMoneyForm.getToAccountId(), //
                    sendMoneyForm.getAmount());
        } catch (BankTransactionException e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());  //add Model to View  (MVC model)
            return "/sendMoneyPage"; 
        }
        return "redirect:/";
    }
 
}
