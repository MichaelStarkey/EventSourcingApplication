from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import pytest
import time


@pytest.fixture(scope="session", autouse=True)
def driver():
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    d = webdriver.Chrome(chrome_options=chrome_options)
    yield d
    d.quit()


def test_authentication_does_login_redirect(driver):
    driver.get("http://127.0.0.1:3000/")
    time.sleep(0.05)
    assert driver.current_url == "http://127.0.0.1:3000/users/login", "No redirect for non authed users"


def test_authentication_can_register(driver):
    driver.get("http://127.0.0.1:3000/users/register")
    time.sleep(0.05)
    driver.find_element_by_name("email").send_keys("test@test.com")
    driver.find_element_by_name("fName").send_keys("test")
    driver.find_element_by_name("lName").send_keys("test")
    driver.find_element_by_name("password").send_keys("test")
    driver.find_element_by_name("password2").send_keys("test")
    time.sleep(0.05)
    driver.find_element_by_id("submit").click()
    time.sleep(0.05)
    assert driver.current_url == "http://127.0.0.1:3000/users/login", "No redirect after registration"


def test_authentication_can_login(driver):
    driver.get("http://127.0.0.1:3000/users/login")
    time.sleep(0.05)
    driver.find_element_by_name("email").send_keys("test@test.com")
    driver.find_element_by_name("password").send_keys("test")
    time.sleep(0.05)
    driver.find_element_by_id("submit").click()
    time.sleep(0.05)
    assert driver.current_url == "http://127.0.0.1:3000/", "Cant login with registered details"


def test_querying_services_can_find_all_accounts(driver):
    driver.get("http://127.0.0.1:3000/")
    time.sleep(0.05)
    assert len(driver.find_elements_by_class_name("accounts")) == 2, "Correct number of accounts not found"


def test_querying_services_can_find_all_transactions(driver):
    driver.get("http://127.0.0.1:3000/account/account_id")
    time.sleep(0.05)
    assert len(driver.find_elements_by_css_selector("tr")) == 4, "Correct number of transactions not found"


def test_transaction_form_does_link(driver):
    driver.get("http://127.0.0.1:3000/")
    time.sleep(0.05)
    driver.find_element_by_class_name("btn-outline-primary").click()
    time.sleep(0.05)
    assert driver.current_url == "http://127.0.0.1:3000/transaction", "Link to transaction form not working"


def test_transaction_form_can_complete_form(driver):
    driver.get("http://127.0.0.1:3000/transaction")
    time.sleep(0.05)
    driver.find_element_by_css_selector("#creditor > option:nth-child(2").click()
    driver.find_element_by_name("debtor").send_keys("test")
    driver.find_element_by_name("amount").send_keys("50")
    time.sleep(0.05)
    driver.find_element_by_class_name("btn-primary").click()
    time.sleep(0.05)
    assert driver.current_url == "http://127.0.0.1:3000/", "Cannot complete transaction form"


def test_account_create_command_does_send(driver):
    driver.get("http://127.0.0.1:3000/")
    time.sleep(0.05)
    driver.find_element_by_class_name("btn-outline-success").click()
    time.sleep(0.05)
    assert driver.current_url == "http://127.0.0.1:3000/", "Create account button not redirecting back to /"
