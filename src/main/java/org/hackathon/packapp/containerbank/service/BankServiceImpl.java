
package org.hackathon.packapp.containerbank.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hackathon.packapp.containerbank.model.Advisor;
import org.hackathon.packapp.containerbank.model.Card;
import org.hackathon.packapp.containerbank.model.CardType;
import org.hackathon.packapp.containerbank.model.Customer;
import org.hackathon.packapp.containerbank.model.Payment;
import org.hackathon.packapp.containerbank.repository.AdvisorRepository;
import org.hackathon.packapp.containerbank.repository.CardRepository;
import org.hackathon.packapp.containerbank.repository.CustomerRepository;
import org.hackathon.packapp.containerbank.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Mostly used as a facade for all ContainerBank controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Wavestone
 */
@Service
public class BankServiceImpl implements BankService {

    private CardRepository cardRepository;
    private AdvisorRepository advisorRepository;
    private CustomerRepository customerRepository;
    private PaymentRepository paymentRepository;

	final Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);

    
    @Autowired
    public BankServiceImpl(CardRepository cardRepository, AdvisorRepository advisorRepository, CustomerRepository customerRepository, PaymentRepository paymentRepository) {
        this.cardRepository = cardRepository;
        this.advisorRepository = advisorRepository;
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CardType> findCardTypes() throws DataAccessException {
        return cardRepository.findCardTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findCustomerById(int id) throws DataAccessException {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Customer> findCustomerByLastName(String lastName) throws DataAccessException {
        return customerRepository.findByLastName(lastName);
    }

    @Override
    @Transactional
    public void saveCustomer(Customer customer) throws DataAccessException {
        customerRepository.save(customer);
    }


    @Override
    @Transactional
    public void savePayment(Payment payment) throws DataAccessException {
        paymentRepository.save(payment);
    }


    @Override
    @Transactional(readOnly = true)
    public Card findCardById(int id) throws DataAccessException {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpGet httpGet = new HttpGet("http://localhost:9093/card/" + id);
    	ObjectMapper objectMapper = new ObjectMapper();
    	Card card = null;
    	try {
    		logger.debug("Sending request to advisor back");
			CloseableHttpResponse advisorsResponse = httpclient.execute(httpGet);
			card = objectMapper.readValue(advisorsResponse.getEntity().getContent(), new TypeReference<Card>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend advisor");
		}
    	return card;
    }

    @Override
    @Transactional
    public void saveCard(Card card) throws DataAccessException {
        cardRepository.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "advisors")
    public Collection<Advisor> findAdvisors() throws DataAccessException {
    	logger.debug("entering find avisors");
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpGet httpGet = new HttpGet("http://localhost:9090/advisor");
    	ObjectMapper objectMapper = new ObjectMapper();
    	Collection<Advisor> advisors = null;
    	try {
    		logger.debug("Sending request to advisor back");
			CloseableHttpResponse advisorsResponse = httpclient.execute(httpGet);
	    	advisors = objectMapper.readValue(advisorsResponse.getEntity().getContent(), new TypeReference<List<Advisor>>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend advisor");
		}
    	return advisors;
    }

	@Override
	public Collection<Payment> findPaymentsByCardId(int cardId) {
		return paymentRepository.findByCardId(cardId);
	}


}
