package jp.co.internous.team2407.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.team2407.model.domain.TblCart;
import jp.co.internous.team2407.model.domain.dto.CartDto;
import jp.co.internous.team2407.model.form.CartForm;
import jp.co.internous.team2407.model.mapper.TblCartMapper;
import jp.co.internous.team2407.model.session.LoginSession;


/**
 * カート情報に関する処理のコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2407/cart")
public class CartController {
	
	/*
	 * フィールド定義
	 */
	
	@Autowired
	private TblCartMapper cartMapper;
	
	@Autowired
	private LoginSession loginSession;
	
	
	private Gson gson = new Gson();
	

	/**
	 * カート画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		
		int userId = loginSession.isLogined() ? loginSession.getUserId() : loginSession.getTmpUserId();

		List<CartDto> cart = cartMapper.findByUserId(userId);
		
		m.addAttribute("carts",cart);
		m.addAttribute("loginSession", loginSession);
		
		return "cart";
		
	}

	/**
	 * カートに追加処理を行う
	 * @param f カート情報のForm
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/add")
	public String addCart(CartForm f, Model m) {
		
		int userId = loginSession.isLogined() ? loginSession.getUserId() : loginSession.getTmpUserId();

		f.setUserId(userId);
		
		TblCart cart = new TblCart();
		
		int result = cartMapper.findCountByUserIdAndProuductId(userId, f.getProductId());
				
		if(result != 0) {
			cart.setUserId(f.getUserId());
			cart.setProductId(f.getProductId());
			cart.setProductCount(f.getProductCount());
			cartMapper.update(cart);
		} else if(result == 0) {
			cart.setUserId(f.getUserId());
			cart.setProductId(f.getProductId());
			cart.setProductCount(f.getProductCount());
			cartMapper.insert(cart);
		}
		List<CartDto> carts = cartMapper.findByUserId(userId);
		
		m.addAttribute("loginSession", loginSession);
		m.addAttribute("carts", carts);
		
		return "cart";
		
	}

	/**
	 * カート情報を削除する
	 * @param checkedIdList 選択したカート情報のIDリスト
	 * @return true:削除成功、false:削除失敗
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/delete")
	@ResponseBody
	public boolean deleteCart(@RequestBody String checkedIdList) {

		CartForm form = gson.fromJson(checkedIdList, CartForm.class);
		
		int result = cartMapper.deleteById(form.getCheckedIdList());
		
		return result > 0;
	}
}
