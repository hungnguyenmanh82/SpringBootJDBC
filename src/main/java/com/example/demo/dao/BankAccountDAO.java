package com.example.demo.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.BankTransactionException;
import com.example.demo.mapper.BankAccountMapper;
import com.example.demo.model.BankAccountInfo;

@Repository
@Transactional
public class BankAccountDAO extends JdbcDaoSupport {

	@Autowired
	public BankAccountDAO(DataSource dataSource) {
		this.setDataSource(dataSource);
	}

	public List<BankAccountInfo> getBankAccounts() {
		// Select ba.Id, ba.Full_Name, ba.Balance From Bank_Account ba
		String sql = BankAccountMapper.BASE_SQL;

		//dùng cho SQL prepare Statement, mỗi item sẽ tương ứng với "?"
		Object[] params = new Object[] {};
		
		//Mapper sẽ nhận ResultSet trả về truy vấn SQL và lấy dữ liệu map Instance ở tầng Model là BankAccountInfo
		BankAccountMapper mapper = new BankAccountMapper();
		
		//mỗi phần tử của List là 1 row (hay resultSet)
		List<BankAccountInfo> list = this.getJdbcTemplate().query(sql, params, mapper);

		return list;
	}

	public BankAccountInfo findBankAccount(Long id) {
		//dùng HQL của Hibernate  (SQL ko dùng cú pháp này)
		// Select ba.Id, ba.Full_Name, ba.Balance From Bank_Account ba
		// Where ba.Id = ?
//		String sql = BankAccountMapper.BASE_SQL + " where ba.Id = ? ";
		
		//cách 2: dùng SQL như bình thường
		String sql = BankAccountMapper.BASE_SQL + " where Id = ? ";

		//dùng cho SQL prepare Statement, mỗi item sẽ tương ứng với "?"
		Object[] params = new Object[] { id };
		
		BankAccountMapper mapper = new BankAccountMapper();
		try {
			BankAccountInfo bankAccount = this.getJdbcTemplate().queryForObject(sql, params, mapper);
			return bankAccount;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	// MANDATORY: Transaction must be created before.
	@Transactional(propagation = Propagation.MANDATORY)
	public void addAmount(Long id, double amount) throws BankTransactionException {
		BankAccountInfo accountInfo = this.findBankAccount(id);
		if (accountInfo == null) {
			throw new BankTransactionException("Account not found " + id);
		}
		double newBalance = accountInfo.getBalance() + amount;
		if (accountInfo.getBalance() + amount < 0) {
			throw new BankTransactionException(
					"The money in the account '" + id + "' is not enough (" + accountInfo.getBalance() + ")");
		}
		accountInfo.setBalance(newBalance);
		// Update to DB
		String sqlUpdate = "Update Bank_Account set Balance = ? where Id = ?";
		this.getJdbcTemplate().update(sqlUpdate, accountInfo.getBalance(), accountInfo.getId());
	}

	// Do not catch BankTransactionException in this method.
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = BankTransactionException.class)
	public void sendMoney(Long fromAccountId, Long toAccountId, double amount) throws BankTransactionException {

		addAmount(toAccountId, amount);
		addAmount(fromAccountId, -amount);
	}
}
