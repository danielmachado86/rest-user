package io.dmcapps.dshopping.user.address;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
@Transactional(REQUIRED)
public class AddressService {
    
    
    @Transactional(SUPPORTS)
    public List<Address> findAllUserAddresses(String user_id) {
        return Address.list(new Document("user", user_id));
    }

    public Address findAddressForUser(String user_id, String name) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user", user_id);
        param.put("name", name);
        return Address.find(new Document(param)).firstResult();
    }
    
    @Transactional(SUPPORTS)
    public Address findAddressById(Long id) {
        return Address.findById(id);
    }
    
    public Address persistAddress(@Valid Address address) {
        Address.persist(address);
        return address;
    }

    public Address updateAddress(@Valid Address address) {
        String params = String.format("{'user': %s, 'name': %s}", address.user, address.name);
		Address entity = Address.find(params).firstResult();
        entity.name = address.name;
        entity.city = address.city;
        entity.address_line1 = address.address_line1;
        entity.address_line2 = address.address_line2;
        entity.reference = address.reference;
        entity.location = address.location;
        entity.favorite = address.favorite;
        return entity;
    }

	public Address findUserAddressByName(String user_id, String name) {
        String params = String.format("{'user': '%s', 'name': '%s'}", user_id, name);
        System.out.println(params);
		return Address.find(params).firstResult();
	}

	public @Valid Address updateAuthUserAddress(String user_id, @Valid Address address) {
		return null;
	}

	public void deleteAddress(String user_id, String name) {
        String params = String.format("{'user': %s, 'name': %s}", user_id, name);
		Address entity = Address.find(params).firstResult();
        entity.delete();
	}
}