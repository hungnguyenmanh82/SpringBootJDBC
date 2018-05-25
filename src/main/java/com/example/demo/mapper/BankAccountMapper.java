package com.example.demo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
 

import org.springframework.jdbc.core.RowMapper;

import com.example.demo.model.BankAccountInfo;

/**
 * RowMapper<BankAccountInfo> => callback sẽ phải return BankAccountInfo
 *
 */
public class BankAccountMapper implements RowMapper<BankAccountInfo> {
     //dùng HQL của Hibernate  (SQL ko dùng cú pháp này)
//    public static final String BASE_SQL = "Select ba.Id, ba.Full_Name, ba.Balance From Bank_Account ba ";
    //dùng SQL như bình thường
	public static final String BASE_SQL = "SELECT Id, Full_Name, Balance FROM Bank_Account ";
 
    /**
     * callback function tương ứng trả về 1 resultSet tương ứng với 1 row từ table trả về từ SQL query
     */
    @Override
    public BankAccountInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
 
        Long id = rs.getLong("Id");
        String fullName = rs.getString("Full_Name");
        double balance = rs.getDouble("Balance");
 
        return new BankAccountInfo(id, fullName, balance);
    }
 
}