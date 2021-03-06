package com.tampro.frontend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tampro.dto.CategoryDTO;
import com.tampro.dto.Paging;
import com.tampro.dto.ProductInStockDTO;
import com.tampro.dto.ProductInfoDTO;
import com.tampro.dto.UserDTO;
import com.tampro.entity.ProductInStock;
import com.tampro.service.CategoryService;
import com.tampro.service.ProductInStockService;
import com.tampro.service.ProductInfoService;
import com.tampro.validator.LoginValidator;

@Controller
public class HomeController {
	@InitBinder
	public void init(WebDataBinder binder) {
		if(binder.getTarget() == null) {
			return;
		}
		if(binder.getTarget().getClass() == UserDTO.class) {
			binder.setValidator(loginValidator);
		}
	}
	
	@Autowired
	CategoryService categoryService;
	@Autowired
	ProductInfoService productInfoService;
	@Autowired
	LoginValidator loginValidator;
	@Autowired
	ProductInStockService productInStockService;
	
	@RequestMapping(value = {"/","/index"})
	public String home(Model model) {
		model.addAttribute("listProdut",productInfoService.getProductNews());
		return "index";
	}
	@GetMapping(value = {"/{url}"})
	public String showProductDetail(Model model,@PathVariable("url") String url) {
		System.out.println(url);
		List<ProductInfoDTO> infoDTOs = productInfoService.getAllByProperty("url", url);
		if(infoDTOs !=null && !infoDTOs.isEmpty()) {
			ProductInfoDTO infoDTO = infoDTOs.get(0);
			ProductInStockDTO productInStockDTO = 	productInStockService.findByProperty("productInfo.id", infoDTO.getId());
			infoDTO.setProductInStockDTO(productInStockDTO);
			model.addAttribute("product",infoDTO);
			return "product-detail";
		}
		return "product-detail";
	}
	@GetMapping(value = {"/the-loai/{url}","/the-loai/{url}/"})
	public String redirect(Model model,@PathVariable("url") String url) {
			return "redirect:/the-loai/"+url+"/1";
	}
	@GetMapping(value = {"/the-loai/{url}/{page}"})
	public String showProductByCategory(Model model,@PathVariable("url") String url,@PathVariable("page") int page,@RequestParam( name = "sortPrice",required = false) String sort) {
		Paging paging = new Paging(24);
		paging.setPageIndex(page);		
		List<CategoryDTO> cates = categoryService.getAllByProperty("url", url);
		if(!cates.isEmpty()) {
			List<ProductInfoDTO> infoDTOs = productInfoService.getAllByPropertySort("category.id", cates.get(0).getId(),paging , sort);
			CategoryDTO categoryDTO = cates.get(0);
			categoryDTO.setProductInfoDTOs(infoDTOs);
			model.addAttribute("category", categoryDTO);
			model.addAttribute("pageInfo", paging);
		}
		return "product-category-list";
	}
	@GetMapping(value = {"/tim-kiem/"})
	public String showProductBySearch(Model model,@RequestParam("s") String search,
			@RequestParam( name = "sort",required = false) String sort ,
			@RequestParam(name = "trang",defaultValue = "1",required = false) int page) {
		
		Paging paging = new Paging(1);
		paging.setPageIndex(page);		
		ProductInfoDTO infoDTO = new  ProductInfoDTO();
		infoDTO.setName(search);
		List<ProductInfoDTO> infoDTOs = productInfoService.getAllSort(infoDTO, paging,sort);
		model.addAttribute("listProduct", infoDTOs);
		model.addAttribute("pageInfo", paging);
		
		return "product-search-list";
	}
}
