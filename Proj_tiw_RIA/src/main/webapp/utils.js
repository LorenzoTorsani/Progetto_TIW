/**
 * AJAX call management
 */

function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
      cback(req)
    }; // closure
    req.open(method, url);
    req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded') // senza questo viene mandato come multipart, ci accedo come getPart che viene ritornato in tipo Part
    if (formElement == null) {
      req.send();
    } else {
		var formData = new FormData(formElement);
      req.send(new URLSearchParams(new FormData(formElement))); // creo nuova variabile URLSearchParams, altrimenti viene inviata come multipart, tramite cui puoi fare getParameter
    }
    if (formElement !== null && reset === true) {
      formElement.reset();
    }
  }

  // formElement è il <form> html 
  // FormData è la codifica del form da usare nelle form