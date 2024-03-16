package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private CustomerDao customerDao;

	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());

		copyPetStoreFields(petStore, petStoreData);

		PetStore savedPetStore = petStoreDao.save(petStore);
		return new PetStoreData(savedPetStore);
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;

		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}

		return petStore;
	}

	@Transactional(readOnly = false)
	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " does not exist."));
	}
	
	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores(){
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> result = new LinkedList<>();
		
		for(PetStore petStore : petStores) {
			PetStoreData storeData = new PetStoreData(petStore);
			
			storeData.getCustomers().clear();
			storeData.getEmployees().clear();
			
			result.add(storeData);
		}
		return result;
	}
	
	public PetStoreData returnPetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		return new PetStoreData(petStore);
	}
	
	public void deletePetStoreById (Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
	}

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee (Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		
		//Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreatePetStoreEmployee(petStoreId, petStoreEmployee.getEmployeeId());

		copyEmployeeFields(employee, petStoreEmployee);
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		Employee savedEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(savedEmployee);

	}

	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
	}
	
	private Employee findOrCreatePetStoreEmployee(Long petStoreId, Long employeeId) {
		Employee employee;

		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(petStoreId, employeeId);
		}
		return employee;
	}

	/*
	 * private Employee findOrCreatePetStoreEmployee(Long petStoreId, Long
	 * employeeId) { Employee employee;
	 * 
	 * return employee.findById(employeeId) .orElseThrow(() -> new
	 * NoSuchElementException("Pet store with ID=" + petStoreId +
	 * " does not exist.")); return employee; }
	 */

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		Employee dbEmployee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + " does not exist."));

		if (dbEmployee.getPetStore().getPetStoreId() != petStoreId) {
			throw new IllegalArgumentException(
					"Employee with ID=" + employeeId + " is not employed at store with ID=" + petStoreId + ".");
		} else {
			return dbEmployee;
		}
	}
	
	@Transactional
	public void deleteEmployeeById(Long petStoreId, Long employeeId) {
	    Employee employee = findEmployeeById(petStoreId, employeeId);
	    if (employee != null && petStoreId.equals(employee.getPetStore().getPetStoreId())) {
	        employeeDao.deleteById(employee.getEmployeeId());
	    } else {
	        throw new NoSuchElementException("Employee with ID=" + employeeId + " does not exist.");
	    }
	}

	
	
	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(petStoreId, customerId, petStoreCustomer.getCustomerEmail());

		copyCustomerFields(customer, petStoreCustomer);
		
		petStore.getCustomers().add(customer);
		customer.getPetStores().add(petStore);
		
		Customer dbCustomer = customerDao.save(customer);

		return new PetStoreCustomer(dbCustomer);

	}

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId, String customerEmail) {
		Customer customer;

		if (Objects.isNull(customerId)) {
			Optional<Customer> existingCustomer = customerDao.findByCustomerEmail(customerEmail);

			if (existingCustomer.isPresent()) {
				throw new DuplicateKeyException("Customer with email address: " + customerEmail + " already exists.");
			}
			customer = new Customer();
		} else {
			customer = findCustomerById(customerId, petStoreId);
		}
		return customer;
	}
	
	private Customer findCustomerById(Long customerId, Long petStoreId) {
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " does not exist."));
		boolean found = false;
		for (PetStore petStore : customer.getPetStores()) {
			if (petStore.getPetStoreId()==(petStoreId)) {
				found = true;
				break;
			} 
		} 
		if(!found) {
			throw new IllegalArgumentException(
					"Pet Store with ID=" + petStoreId + " not found for the Customer with ID=" + customerId);
		}
		return customer;
	}

	private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {

		customer.setCustomerId(petStoreCustomer.getCustomerId());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
	}

	@Transactional
	public void deleteCustomerById(Long petStoreId, Long customerId) {
	    Customer customer = findCustomerById(petStoreId, customerId);
	    if (customer != null && petStoreId.equals(customer.getPetStoreId())) {
	        customerDao.deleteById(customer.getCustomerId());
	    } else {
	        throw new NoSuchElementException("Customer with ID=" + customerId + " does not exist.");
	    }
	}
}
