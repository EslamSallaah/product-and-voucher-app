package com.example.voucherapp.controllers;

import org.springframework.stereotype.Controller;

import com.example.voucherapp.model.voucher;
import com.example.voucherapp.repo.voucherrepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;



@Controller 
public class VoucherController {

    private final voucherrepo repo;

    VoucherController(voucherrepo repo) {
        this.repo = repo;
    }

    @GetMapping("/showCreateVoucher")
    public String showCreateVoucher( ) {

        return "createVoucher";
    }


    @PostMapping("/saveVoucher")
    public String save(voucher voucher) {
        repo.save(voucher);

        
        return "createPesponse";
    }
    @GetMapping("/showGetVoucher")
    public String showGetVoucher() {
        return "getVoucher";
    }
    

    @PostMapping("/getVoucher")
    public ModelAndView getVoucher ( String code) {

        ModelAndView mav= new ModelAndView("voucherDetails");
        System.out.println(code);
mav.addObject(repo.findByCode(code));
        return mav;
    }
    
    
    
}
