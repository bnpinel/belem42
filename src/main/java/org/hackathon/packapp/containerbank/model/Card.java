
package org.hackathon.packapp.containerbank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Simple business object representing a card.
 *
 * @author Wavestone
 */
public class Card extends NamedEntity {

	@DateTimeFormat(pattern = "yyyy/MM/dd")
    private String birthDate;

    private String cardTypeID;

    private String customerID;
    
    private CardType type;
    
    private Customer customer;

    private Set<Payment> payments;

    
    protected Set<Payment> getPaymentsInternal() {
        if (this.payments == null) {
            this.payments = new HashSet<>();
        }
        return this.payments;
    }

    protected void setPaymentsInternal(Set<Payment> payments) {
        this.payments = payments;
    }

    public List<Payment> getPayments() {
        List<Payment> sortedPayments = new ArrayList<>(getPaymentsInternal());
        PropertyComparator.sort(sortedPayments, new MutableSortDefinition("date", false, false));
        return Collections.unmodifiableList(sortedPayments);
    }

    public void addPayment(Payment payment) {
        getPaymentsInternal().add(payment);
        payment.setCard(this);
    }
    
    

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return this.birthDate;
    }

	public String getCardTypeID() {
		return cardTypeID;
	}

	public void setCardTypeID(String cardTypeID) {
		this.cardTypeID = cardTypeID;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

	public CardType getType() {
		return type;
	}

	public void setType(CardType type) {
		this.type = type;
	}

	
}
