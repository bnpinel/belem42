
package org.hackathon.packapp.containerbank.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
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

import com.fasterxml.jackson.core.JsonProcessingException;
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
    	
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpGet httpGet = new HttpGet("http://localhost:9093/cardtype");
    	ObjectMapper objectMapper = new ObjectMapper();
    	Collection<CardType> cardtypelist = null;
    	try {
    		logger.debug("Sending request to card back");
			CloseableHttpResponse cardsResponse = httpclient.execute(httpGet);
			cardtypelist = objectMapper.readValue(cardsResponse.getEntity().getContent(), new TypeReference<List<CardType>>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend card");
			e.printStackTrace();
		}
    	
        return cardtypelist;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer findCustomerById(final String id) {
    	final CloseableHttpClient httpclient = HttpClients.createDefault();
    	final HttpGet httpGet = new HttpGet("http://localhost:9091/customers/" + id);    	
    	final ObjectMapper objectMapper = new ObjectMapper();
    	Customer customer = null;
    	try {
    		logger.debug("Sending request to customers back");
			CloseableHttpResponse customerResponse = httpclient.execute(httpGet);
			customer = objectMapper.readValue(customerResponse.getEntity().getContent(), new TypeReference<Customer>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend customers");
			e.printStackTrace();
		}
    	
    	// Populate object with cards
    	HttpGet httpGetCards = new HttpGet("http://localhost:9093/card/customer/" + id);
    	ObjectMapper objectMapperCards = new ObjectMapper();
    	Collection<Card> cards = null;
    	try {
    		logger.debug("Sending request to card back");
			CloseableHttpResponse cardsResponse = httpclient.execute(httpGetCards);
			cards = objectMapperCards.readValue(cardsResponse.getEntity().getContent(), new TypeReference<List<Card>>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend card");
			e.printStackTrace();
		}
    	
    	if (cards!=null && !cards.isEmpty()) {
    		for (Iterator<Card> iterator = cards.iterator(); iterator.hasNext();) {
				customer.addCard(iterator.next());
			}
    	}
    	
    	
    	
    	return customer;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Customer> findCustomerByLastName(String lastName) {
    	final CloseableHttpClient httpclient = HttpClients.createDefault();
    	final HttpGet httpGet = new HttpGet("http://localhost:9091/customers/?name=" + lastName);
    	final ObjectMapper objectMapper = new ObjectMapper();
    	Collection<Customer> customers = null;
    	try {
    		logger.debug("Sending request to customers back");
			CloseableHttpResponse customersResponse = httpclient.execute(httpGet);
			customers = objectMapper.readValue(customersResponse.getEntity().getContent(), new TypeReference<Collection<Customer>>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend customers");
			e.printStackTrace();
		}
    	return customers;
    }

    @Override
    @Transactional
    public String saveCustomer(Customer customer) {
    	final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    	final List<Header> headers = Arrays.asList(new Header[] {header});
    	final CloseableHttpClient httpclient = HttpClients.custom().setDefaultHeaders(headers).build();
    	final ObjectMapper objectMapper = new ObjectMapper();
    	final String url = "http://localhost:9091/customers/";
    	
    	try {
	    	// update
	    	if(customer.getId() != null) {
	    		final HttpPut httpRequest = new HttpPut(url + customer.getId());
	    		httpRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(customer)));
	    		CloseableHttpResponse customerResponse = httpclient.execute(httpRequest);
	    		customer = objectMapper.readValue(customerResponse.getEntity().getContent(), new TypeReference<Customer>() { });
	    	} else { // create
	    		final HttpPost httpRequest = new HttpPost(url);
	    		httpRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(customer)));
	    		CloseableHttpResponse customerResponse = httpclient.execute(httpRequest);
	    		customer = objectMapper.readValue(customerResponse.getEntity().getContent(), new TypeReference<Customer>() { });
	    	}
    	} catch(final IOException ioe) {
    		logger.error("Impossible de contacter le backend customer.");
    		ioe.printStackTrace();
    	}
    	return customer.getId();
    }


    @Override
    @Transactional
    public void savePayment(Payment payment) throws DataAccessException {
        paymentRepository.save(payment);
    }


    @Override
    @Transactional(readOnly = true)
    public Card findCardById(int id) {
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	HttpGet httpGet = new HttpGet("http://localhost:9093/card/" + id);
    	ObjectMapper objectMapper = new ObjectMapper();
    	Card card = null;
    	try {
    		logger.debug("Sending request to card back");
			CloseableHttpResponse cardsResponse = httpclient.execute(httpGet);
			card = objectMapper.readValue(cardsResponse.getEntity().getContent(), new TypeReference<Card>() { });
    	} catch (IOException e) {
			logger.error("Impossible de contacter le backend card");
			e.printStackTrace();
		}
    	return card;
    }

    @Override
    @Transactional
    public void saveCard(Card card) {
   	
    	final Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    	final List<Header> headers = Arrays.asList(new Header[] {header});
    	final CloseableHttpClient httpclient = HttpClients.custom().setDefaultHeaders(headers).build();
    	
    	if(card.getId()==null || card.getId().trim().equals("")) {

    		// POST
    		
        	HttpPost httpPost = new HttpPost("http://localhost:9093/card");
        	ObjectMapper objectMapper = new ObjectMapper();
        	try {
     		
        		httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(card)));
				
	        	try {
	        		logger.debug("Sending request to card back");
	    			CloseableHttpResponse cardsResponse = httpclient.execute(httpPost);
	    			
	    			if(cardsResponse.getStatusLine().getStatusCode() != 201) {
		    			logger.error("Impossible de contacter le backend card (HTTP " + cardsResponse.getStatusLine().getStatusCode() + ")");
	    			}
	    			
	        	} catch (IOException e) {
	    			logger.error("Impossible de contacter le backend card");
	    			e.printStackTrace();
	    		}
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    		
    	} else {
    		
    		//PUT
    		
        	HttpPut httpPut = new HttpPut("http://localhost:9093/card/" + card.getId());
        	ObjectMapper objectMapper = new ObjectMapper();
        	try {
        		httpPut.setEntity(new StringEntity(objectMapper.writeValueAsString(card)));
				
	        	try {
	        		logger.debug("Sending request to card back");
	    			CloseableHttpResponse cardsResponse = httpclient.execute(httpPut);
	    			card = objectMapper.readValue(cardsResponse.getEntity().getContent(), new TypeReference<Card>() { });
	        	} catch (IOException e) {
	    			logger.error("Impossible de contacter le backend card");
	    			e.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
	    		}
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}
    		
    	}
        
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
