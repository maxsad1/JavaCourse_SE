package com.javacourse.contact.dao;

import com.javacourse.contact.entity.Contact;
import com.javacourse.contact.exception.ContactDAOException;
import com.javacourse.contact.filter.ContactFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactDAOFileCsv implements ContactDAO
{
    @Override
    public Long addContact(Contact contact) throws ContactDAOException {
        contact.setContactId(System.currentTimeMillis());
        List<Contact> contacts = loadContacts();
        contacts.add(contact);
        saveContacts(contacts);
        return contact.getContactId();
    }

    @Override
    public void updateContact(Contact contact) {
    }

    @Override
    public void deleteContact(Long contactId) {
    }

    @Override
    public Contact getContact(Long contactId) {
        List<Contact> contacts = loadContacts();
        for(Contact con : contacts) {
            if(contactId.equals(con.getContactId())) {
                return con;
            }
        }
        return null;
    }

    @Override
    public List<Contact> findContact(ContactFilter filter) {
        return loadContacts();
    }

    private List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<Contact>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("contacts.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    Contact cnt = new Contact();
                    cnt.setContactId(Long.parseLong(parts[0]));
                    cnt.setSurName(parts[1]);
                    cnt.setGivenName(parts[2]);
                    cnt.setEmail(parts[3]);
                    cnt.setPhone(parts[4]);

                    contacts.add(cnt);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private void saveContacts(List<Contact> contacts) {

    }
}
