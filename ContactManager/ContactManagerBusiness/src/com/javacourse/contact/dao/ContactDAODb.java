package com.javacourse.contact.dao;

import com.javacourse.contact.entity.Contact;
import com.javacourse.contact.exception.ContactDAOException;
import com.javacourse.contact.filter.ContactFilter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ContactDAODb implements ContactDAO {
    private static final String SELECT = "SELECT * FROM cm_contact";
    private static final String ORDER = "ORDER BY sur_name, given_name";
    private static final String SELECT_ONE = "SELECT * FROM cm_contact WHERE contact_id=?";
    private static final String INSERT = "INSERT INTO cm_contact (sur_name, given_name, email, phone) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE cm_contact SET sur_name=?, given_name=?, email=?, phone=? WHERE contact_id=?";
    private static final String DELETE = "DELETE FROM cm_contact WHERE contact_id=?";

    private Connection getConnection() throws SQLException {
//        String url = "jdbc:postgresql://localhost:5432/jvacourse";
//        return DriverManager.getConnection(url, "postgres", "postgres");
        try {
            Context ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("java:comp/env/regionDS");
//            DataSource dataSource = (DataSource) ctx.lookup("jdbc/regionDS");
            Connection con = dataSource.getConnection();
            return con;
        } catch (NamingException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Long addContact(Contact contact) throws ContactDAOException {
        Long contactId = 0L;
        try {
            Connection con = getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(INSERT, new String[]{"contact_id"});
                stmt.setString(1, contact.getSurName());
                stmt.setString(2, contact.getGivenName());
                stmt.setString(3, contact.getEmail());
                stmt.setString(4, contact.getPhone());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    contactId = rs.getLong("contact_id");
                    contact.setContactId(contactId);
                }
                rs.close();

                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return contactId;
    }

    @Override
    public void updateContact(Contact contact) {
        try {
            Connection con = getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(UPDATE);
                stmt.setString(1, contact.getSurName());
                stmt.setString(2, contact.getGivenName());
                stmt.setString(3, contact.getEmail());
                stmt.setString(4, contact.getPhone());
                stmt.setLong(5, contact.getContactId());
                stmt.executeUpdate();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    @Override
    public void deleteContact(Long contactId) {
        try {
            Connection con = getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(DELETE);
                stmt.setLong(1, contactId);
                stmt.executeUpdate();
                stmt.close();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    @Override
    public Contact getContact(Long contactId) {
        Contact cnt = new Contact();
        try {
            Connection con = getConnection();
            try {
                PreparedStatement stmt = con.prepareStatement(SELECT_ONE);
                stmt.setLong(1, contactId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cnt = buildContact(rs);
                }
                stmt.close();
                rs.close();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return cnt;
    }

    @Override
    public List<Contact> findContact(ContactFilter filter) {
        List<Contact> result = new LinkedList<Contact>();
        try {
            Connection con = getConnection();
            try {
                String sql = SELECT;
                if (filter != null && filter.getSurName() != null && !filter.getSurName().trim().isEmpty()) {
                    sql += " WHERE sur_name like ?";
                }
                sql += " " + ORDER;
                PreparedStatement stmt = con.prepareStatement(sql);
                if (filter != null && filter.getSurName() != null && !filter.getSurName().trim().isEmpty()) {
                    stmt.setString(1, '%' + filter.getSurName() + '%');
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Contact cnt = buildContact(rs);
                    result.add(cnt);
                }
                stmt.close();
                rs.close();
            } finally {
                con.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return result;
    }

    private Contact buildContact(ResultSet rs) throws SQLException {
        Contact cnt = new Contact();
        cnt.setContactId(rs.getLong("contact_id"));
        cnt.setGivenName(rs.getString("given_name"));
        cnt.setSurName(rs.getString("sur_name"));
        cnt.setEmail(rs.getString("email"));
        cnt.setPhone(rs.getString("phone"));
        return cnt;
    }
}
