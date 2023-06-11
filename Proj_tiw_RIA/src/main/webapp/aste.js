{
	let vendo, acquisto, goToVendo, goToAcquisto, listaByKeyword,
		pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			console.log(getIdFromCookieSet(sessionStorage.getItem("username")));
			console.log(returnLastValueCookie(sessionStorage.getItem("username")));

			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh(); // display initial content

		}
	}, false);

	// carica la pagina vendo
	function GoToVendo(_alert, _openlistcontainer, _openlistcontainerbody, _closedlistcontainer, _closedlistcontainerbody,
		_articolicontainer, _articolicontainerbody, _astewizard, _detailcontainer, _detailcontainerbody) {
		this.alert = _alert;
		this.openlistcontainer = _openlistcontainer;
		this.openlistcontainerbody = _openlistcontainerbody;
		this.closedlistcontainer = _closedlistcontainer;
		this.closedlistcontainerbody = _closedlistcontainerbody;
		this.articolicontainer = _articolicontainer;
		this.articolicontainerbody = _articolicontainerbody;
		this.astewizard = _astewizard,
			this.detailcontainer = _detailcontainer,
			this.detailcontainerbody = _detailcontainerbody;

		var now = new Date(),
			formattedDate = now.toISOString().substring(0, 10);

		this.reset = function() {
			this.openlistcontainer.style.visibility = "hidden";
			this.closedlistcontainer.style.visibility = "hidden";
			this.articolicontainer.style.visibility = "hidden";

		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "Vendo", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var asteToShow = JSON.parse(req.responseText);
							if (asteToShow.asteAperte.length == 0) {
								self.alert.textContent = "Nessuna asta aperta";
								return;
								// TODO togliere il return o gestirlo in un altro modo
							}
							if (asteToShow.asteChiuse.length == 0) {
								self.alert.textContent = "Nessuna asta chiusa";
								return;
							}
							console.log(window.location.pathname + window.location.search);
							self.updateAsteAperte(asteToShow.asteAperte, asteToShow.articoli);
							self.updateAsteChiuse(asteToShow.asteChiuse);
							self.updateArticoli(asteToShow.articoli);
							self.updateAsteWizard(asteToShow.articoli);
							if (next) next();

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						} else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};

		// mostra lista aste aperte di un utente
		this.updateAsteAperte = function(arrayAste, arrayArticoli) {
			var elem, i, row, destcell, datecell, linkcell, anchor;
			this.openlistcontainerbody.innerHTML = ""; // empty the table body
			// build updated list
			var self = this;
			arrayAste.forEach(function(asta) { // self visible here, not this
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = asta.idAsta;
				row.appendChild(destcell);

				datecell = document.createElement("td");
				datecell.textContent = asta.scad;
				row.appendChild(datecell);


				datecell = document.createElement("td");
				datecell.textContent = asta.tempoMancante;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				datecell.textContent = asta.prezzoIniziale;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				datecell.textContent = asta.rialzoMinimo;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				if (asta.offertaMax > 0) {
					datecell.textContent = asta.offertaMax;
				} else {
					datecell.textContent = "Ancora nessuna offerta";
				}
				row.appendChild(datecell);

				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Show");
				anchor.appendChild(linkText);

				// TODO è giusto asta_id ?
				anchor.setAttribute('asta_id', asta.idAsta); // set a custom HTML attribute
				anchor.addEventListener("click", (e) => {
					astaApertaDetails.show(e.target.getAttribute("asta_id"));
					listaOfferteAsteAperte.reset();
					listaOfferteAsteAperte.show(e.target.getAttribute("asta_id"));
				}, false);
				anchor.href = "#";
				self.openlistcontainerbody.appendChild(row);
				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Dettagli");
				anchor.appendChild(linkText);
				// make list item clickable
				anchor.setAttribute('asta_id', asta.idAsta);
				anchor.addEventListener("click", () => {
					// quando clicco chiudo e aggiorno
					//	listaAsteChiuse.reset();
					//	listaAsteAperte.reset();
					//	listaAsteAperte.close(e.target.getAttribute("asta_id"));
					self.showDettagliAsta(asta.idAsta);
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);

				// creo cella con lista codici articoli
				datecell = document.createElement("td");
				let s = " ";
				arrayArticoli.forEach(function(articolo) {
					if (asta.idAsta === articolo.idasta) {
						// console.log(articolo.code);
						s = s + articolo.code + " ";
					}
				});
				datecell.textContent = s;
				row.appendChild(datecell);

				// creo cella con lista nomi articoli
				datecell = document.createElement("td");
				s = " ";
				arrayArticoli.forEach(function(articolo) {
					if (asta.idAsta === articolo.idasta) {
						// console.log(articolo.code);
						s = s + articolo.name + " ";
					}
				});
				datecell.textContent = s;
				row.appendChild(datecell);

				self.openlistcontainerbody.appendChild(row);
			});
			this.openlistcontainer.style.visibility = "visible";

		}

		// mostra lista aste chiuse di un utente
		this.updateAsteChiuse = function(arrayAste) {
			var row, destcell, datecell, linkcell, anchor, linkText;
			var self = this;
			this.closedlistcontainerbody.innerHTML = "";

			arrayAste.forEach(function(asta) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = asta.idAsta;
				row.appendChild(destcell);

				datecell = document.createElement("td");
				datecell.textContent = asta.scad;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				datecell.textContent = asta.prezzoIniziale;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				datecell.textContent = asta.offertaMax;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				datecell.textContent = asta.aggiudicatario;
				row.appendChild(datecell);

				datecell = document.createElement("td");
				datecell.textContent = asta.indirizzo;
				row.appendChild(datecell);

				self.closedlistcontainerbody.appendChild(row);
			});

			this.closedlistcontainer.style.visibility = "visible";
		}


		// mostra la lista degli articoli di un utente
		this.updateArticoli = function(arrayArticoli) {
			var row, destcell, linkcell;
			var self = this;
			this.articolicontainerbody.innerHTML = "";

			arrayArticoli.forEach(function(articolo) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = articolo.code;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.name;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.description;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.price;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				if (articolo.sold) {
					destcell.textContent = "venduto";
				} else {
					destcell.textContent = "non venduto";

				}
				row.appendChild(destcell);

				// Crea una cella con l'immagine
				linkcell = document.createElement("td");
				const img = document.createElement("img");
				img.src = "/Proj_tiw_RIA/resources/static/images/" + articolo.image;
				linkcell.appendChild(img);
				linkcell.classList.add("immagine"); // Aggiungi la classe "immagine" al <td>
				row.appendChild(linkcell);


				self.articolicontainerbody.appendChild(row);
			});

			this.articolicontainer.style.visibility = "visible";
		}

		this.showDettagliAsta = function(_idAsta, next) {
			var self = this;
			this.idAsta = _idAsta;
			makeCall("GET", "GoToDettaglioAsta?idAsta=" + this.idAsta, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var dettagli = JSON.parse(req.responseText);
							if (dettagli.length == 0) {
								self.alert.textContent = "Offerte non disponibili";
								return;
							}
							self.updateDettagliAsta(dettagli, _idAsta); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			)

		}

		this.updateDettagliAsta = function(offerte, idasta) {
			var row, destcell;
			var self = this;
			this.articolicontainerbody.innerHTML = "";

			if (this.detailcontainer.style.display == "block") {
				this.detailcontainer.style.display = "none";
				return;
			}
			var i = true;
			offerte.forEach(function(offerta) {
				// Verifica se la riga esiste già
				var existingRow = Array.from(self.detailcontainerbody.children).find(function(row) {
					var offerenteCell = row.querySelector("td:nth-child(1)");
					var offertaCell = row.querySelector("td:nth-child(2)");
					var dataCell = row.querySelector("td:nth-child(3)");
					return (
						offerenteCell.textContent == offerta.offerente &&
						offertaCell.textContent == offerta.offerta &&
						dataCell.textContent == offerta.data
					);
				});

				if (!existingRow) {
					row = document.createElement("tr");
					destcell = document.createElement("td");
					destcell.textContent = offerta.offerente;
					row.appendChild(destcell);
					destcell = document.createElement("td");
					destcell.textContent = offerta.offerta;
					row.appendChild(destcell);
					destcell = document.createElement("td");
					destcell.textContent = offerta.data;
					row.appendChild(destcell);
					if (i) {
						i = false;
						destcell = document.createElement("td");
						var form = document.createElement("form");
						var fieldset = document.createElement("fieldset");
						var button = document.createElement("input");
						button.setAttribute("type", "button");
						button.setAttribute("value", "chiudi");
						var hidden = document.createElement("input");
						hidden.setAttribute("type", "hidden");
						hidden.setAttribute("name", "idAsta");
						hidden.setAttribute("value", idasta);
						button.setAttribute("id", "chiudiastabutton");
						destcell.appendChild(form);
						form.appendChild(fieldset);
						fieldset.appendChild(button);
						fieldset.appendChild(hidden);
						row.appendChild(destcell);
					}
					self.detailcontainerbody.appendChild(row);
				}
			});

			this.detailcontainer.style.display = "block";
		}


		// form per creare un'asta
		this.updateAsteWizard = function(arrayArticoli) {
			var self = this;
			this.astewizard.innerHTML = "";
			var fieldset = document.createElement("fieldset");
			var scadenzaInput = document.createElement("input");
			scadenzaInput.setAttribute("type", "text");
			scadenzaInput.setAttribute("name", "scadenza");
			scadenzaInput.setAttribute("required", "required");
			var label1 = document.createElement("label");
			label1.textContent = "Scadenza: ";
			fieldset.appendChild(label1);
			fieldset.appendChild(scadenzaInput);
			fieldset.appendChild(document.createElement("br"));

			var rialzoMinimoInput = document.createElement("input");
			rialzoMinimoInput.setAttribute("type", "number");
			rialzoMinimoInput.setAttribute("name", "rialzoMinimo");
			rialzoMinimoInput.setAttribute("step", "1");
			rialzoMinimoInput.setAttribute("min", "1");
			rialzoMinimoInput.setAttribute("required", "required");
			var label2 = document.createElement("label");
			label2.textContent = "Rialzo minimo: ";
			fieldset.appendChild(label2);
			fieldset.appendChild(rialzoMinimoInput);
			fieldset.appendChild(document.createElement("br"));

			arrayArticoli.forEach(function(articolo) {
				if (!articolo.sold) {
					var checkbox = document.createElement("input");
					checkbox.setAttribute("type", "checkbox");
					checkbox.setAttribute("name", "articoli");
					checkbox.setAttribute("id", articolo.code);
					checkbox.setAttribute("value", articolo.code);

					if (articolo.idasta != null) {
						checkbox.setAttribute("disabled", "disabled");
					}

					var label = document.createElement("label");
					label.textContent = articolo.name;

					fieldset.appendChild(checkbox);
					fieldset.appendChild(label);
					fieldset.appendChild(document.createElement("br"));
				}
			});



			var submitButton = document.createElement("input");
			submitButton.setAttribute("type", "button");
			submitButton.setAttribute("name", "submit");
			submitButton.setAttribute("value", "submit");

			fieldset.appendChild(submitButton);
			fieldset.appendChild(document.createElement("br"));

			this.astewizard.appendChild(fieldset);

			this.astewizard.style.visibility = "visible";
		}

		this.registerEvent2 = function(orchestrator) {
			this.detailcontainerbody.addEventListener("click", (e) => {
				if (e.target && e.target.matches("input[type='button']")) {
					var eventfieldset = e.target.closest("fieldset"),
						valid = true;
					for (i = 0; i < eventfieldset.elements.length; i++) {
						if (!eventfieldset.elements[i].checkValidity()) {
							eventfieldset.elements[i].reportValidity();
							valid = false;
							break;
						}
					}
					if (valid) {
						var self = this;
						console.log(e.target.closest("form"));
						var formData = new FormData(e.target.closest("form"));
						for (var pair of formData.entries()) {
							console.log(pair[0] + ', ' + pair[1]);
						}
						makeCall("POST", 'chiudiAsta', e.target.closest("form"),
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText; // error message or mission id
									if (req.status == 200) {
										orchestrator.refresh(message);
									}
									if (req.status == 403) {
										window.location.href = req.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
									}
									else if (req.status != 200) {
										self.alert.textContent = message;
									}
								}
							});
					}
				}
			});
		}

		this.registerEvent1 = function(orchestrator) {
			this.astewizard.addEventListener('click', (e) => {
				if (e.target && e.target.matches("input[type='button']")) {
					var eventfieldset = e.target.closest("fieldset"),
						valid = true;
					for (i = 0; i < eventfieldset.elements.length; i++) {
						if (!eventfieldset.elements[i].checkValidity()) {
							eventfieldset.elements[i].reportValidity();
							valid = false;
							break;
						}
					}
					if (valid) {
						var self = this;
						console.log(e.target.closest("form"));
						var formData = new FormData(e.target.closest("form"));
						for (var pair of formData.entries()) {
							console.log(pair[0] + ', ' + pair[1]);
						}
						makeCall("POST", 'CreateAsta', e.target.closest("form"),
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText; // error message or mission id
									if (req.status == 200) {
										orchestrator.refresh(message);
									}
									if (req.status == 403) {
										window.location.href = req.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
									}
									else if (req.status != 200) {
										self.alert.textContent = message;
										self.reset();
									}
								}
							});
						if (cookieExistence(sessionStorage.getItem("username"))) {
							var oldCookie = getCookieValue(sessionStorage.getItem("username"));
							updateOldCookie(sessionStorage.getItem("username"), oldCookie + "vendo" + ",");
						}
						else {
							createNewCookie(sessionStorage.getItem("username"), "vendo" + ",");
						}
						console.log(getCookieValue(sessionStorage.getItem("username")));
					}
				}

			});
		}

	}

	// carica la pagina acquisto
	function GoToAcquisto(_alertContainer, _aggiudicatelistcontainer, _aggiudicatelistcontainerbody) {
		this.alert = _alertContainer;
		this.aggiudicatelistcontainer = _aggiudicatelistcontainer;
		this.aggiudicatelistcontainerbody = _aggiudicatelistcontainerbody;

		this.reset = function() {
			this.aggiudicatelistcontainer.style.visibility = "hidden";

		}

		this.show = function(next) {
			var self = this;
			makeCall("GET", "Acquisto", null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var asteToShow = JSON.parse(req.responseText);
							if (asteToShow.length == 0) {
								self.alert.textContent = "Nessuna asta aggiudicata!";
								return;
							}
							// console.log(asteToShow);
							self.update(asteToShow); // self visible by closure
							if (next) next(); // show the default element of the list if present

						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						}
						else {
							self.alert.textContent = message;
						}
					}
				}
			);
		};

		// creo lista aste aggiudicate
		this.update = function(arrayAste) {
			var row, destcell;
			this.aggiudicatelistcontainerbody.innerHTML = "";

			var self = this;
			arrayAste.forEach(function(asta) {
				// console.log(asta);
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = asta.idAsta;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.scad;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.prezzoIniziale;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.rialzoMinimo;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.offertaMax;
				row.appendChild(destcell);

				self.aggiudicatelistcontainerbody.appendChild(row);
			});

			this.aggiudicatelistcontainer.style.visibility = "visible";

		}
	}

	// form per creare un nuovo articolo
	function ArticoliWizard(wizardId, alert) {
		this.wizard = wizardId;
		this.alert = alert;

		this.registerEvents = function(orchestrator) {
			this.wizard.querySelector("input[type='submit']").addEventListener('click', (e) => {
				var eventfieldset = e.target.closest("fieldset"),
					valid = true;
				for (i = 0; i < eventfieldset.elements.length; i++) {
					if (!eventfieldset.elements[i].checkValidity()) {
						eventfieldset.elements[i].reportValidity();
						valid = false;
						break;
					}
				}
				if (valid) {
					var self = this;
					makeCall("POST", 'CreateArticolo', e.target.closest("form"),
						function(req) {
							if (req.readyState == XMLHttpRequest.DONE) { }
							var message = req.responseText;
							if (req.status == 200) {
								orchestrator.refresh(message);
							}
							if (req.status == 403) {
								window.location.href = req.getResponseHeader("Location");
								window.sessionStorage.removeItem('username');
							}
							else if (req.status != 200) {
								self.alert.textContent = message;
								self.reset();
							}
						}
					);
				}
			});
			this.reset = function() {
				var fieldsets = document.querySelectorAll("#" + this.wizard.id + " fieldset");
				fieldsets[0].hidden = false;
				//fieldsets[1].hidden = true;
				//fieldsets[2].hidden = true;
			}
		}
	}

	// form per cercare un'asta tramita una parola chiave
	function AstaByKeywordForm(astabykeywordformId, alertContainer) {
		this.astabykeywordformId = astabykeywordformId;
		this.alert = alertContainer;
		this.keyword = null;

		// perche faccio due makeCall?

		this.registerEvent = function(orchestrator) {
			this.astabykeywordformId.querySelector("input[type='button']").addEventListener('click', (e) => {
				var self = this;
				this.keyword = document.getElementById("id_cercaform").elements['parola'].value;
				console.log(this.keyword);
				console.log(e.target.closest("form"));
				var formData = new FormData(e.target.closest("form"));
				for (var pair of formData.entries()) {
					console.log(pair[0] + ', ' + pair[1]);
				}
				document.getElementById("asteRicercateMacrotable").style.display = "block";
				makeCall("POST", 'cercaAstaPerParola', e.target.closest("form"),
					function(req) {
						if (req.readyState == XMLHttpRequest.DONE) {
							var message = req.responseText; // error message or mission id
							if (req.status == 200) {
								//salvo la keyword
								//this.keyword = document.getElementById("keyword").value;
								var asteKeyword = JSON.parse(req.responseText);
								//resetta le possibili offerte gia stampate a schermo
								orchestrator.refresh(message);
								listaByKeyword.update(asteKeyword);
							}
							else {
								self.alert.textContent = message;

							}
						}
					});
			});
		};

		this.updateOfferteAfterOffertaCorrect = function() {
			makeCall("POST", "cercaAstaPerParola?parola=" + this.keyword, null,
				function(req) {
					if (req.readyState == XMLHttpRequest.DONE) {
						var message = req.responseText; // error message or mission id
						if (req.status == 200) {
							var asteKeyword = JSON.parse(req.responseText);
							//resetta le possibili offerte gia stampate a schermo
							listaByKeyword.update(asteKeyword);
						}
						else {
							self.alert.textContent = message;
						}
					}
				});
		}
	}

	// mostra lista di aste aperte che contengono la keyword
	function ListAsteByKeyword(_listaAsteKeyword, _alert, _listKeywordContainerBody, _makeOfferTable, _articoliAstaOfferta, _articoliAstaOffertaBody, _offerteAstaOfferta, _offerteAstaOffertaBody, _makeOfferta) {
		this.alert = _alert;
		this.listaAsteKeyword = _listaAsteKeyword;
		this.listKeywordContainerBody = _listKeywordContainerBody;
		this.makeOfferTable = _makeOfferTable;
		this.articoliAstaOfferta = _articoliAstaOfferta;
		this.articoliAstaOffertaBody = _articoliAstaOffertaBody;
		this.offerteAstaOfferta = _offerteAstaOfferta;
		this.offerteAstaOffertaBody = _offerteAstaOffertaBody;
		this.makeOfferta = _makeOfferta;

		this.reset = function() {
			this.listaAsteKeyword.style.visibility = "hidden";
		}

		this.update = function(arrayAsteKeyword) {
			var row, destcell, datecell, linkcell, anchor;
			this.listKeywordContainerBody.innerHTML = "";
			var self = this;
			arrayAsteKeyword.forEach(function(asta) {
				row = document.createElement("tr");

				destcell = document.createElement("td");
				destcell.textContent = "";
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.idAsta;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.scad;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.tempoMancante;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.prezzoIniziale;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.rialzoMinimo;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				if (asta.offertaMax === -1) {
					destcell.textContent = "Ancora nessuna offerta";
				} else {
					destcell.textContent = asta.offertaMax;
				}
				row.appendChild(destcell);

				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Fai offerta");
				anchor.appendChild(linkText);
				anchor.setAttribute('idasta', asta.idAsta);
				anchor.addEventListener("click", () => {
					self.showOffertaForm(asta.idAsta);
					if (cookieExistence(sessionStorage.getItem("username"))) {
						var oldCookie = getCookieValue(sessionStorage.getItem("username"));
						updateOldCookie(sessionStorage.getItem("username"), oldCookie + asta.idAsta + ",");
					}
					else {
						createNewCookie(sessionStorage.getItem("username"), asta.idAsta + ",");
					}
					console.log(getCookieValue(sessionStorage.getItem("username")));
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				self.listKeywordContainerBody.appendChild(row);
			});
			this.listaAsteKeyword.style.visibility = "visible";
		}

		this.showOffertaForm = function(_idAsta, next) {
			var self = this;
			this.idAsta = _idAsta;
			makeCall("GET", "GoToOfferta?idasta=" + this.idAsta, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var rispostaJson = JSON.parse(req.responseText);
							if (rispostaJson.length == 0) {
								self.alert.textContent = "Offerte non disponibili";
								return;
							}
							self.updateOffertaForm(rispostaJson);
							if (next) next();
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						} else {
							self.alert.textContent = message;
						}
					}
				}
			)
		}

		this.updateOffertaForm = function(rispostaJson) {
			var row, destcell;
			var self = this;
			this.articoliAstaOffertaBody.innerHTML = "";
			this.offerteAstaOffertaBody.innerHTML = "";

			if (this.makeOfferTable.style.display == "block") {
				this.makeOfferTable.style.display = "none";
				return;
			}
			rispostaJson.articoli.forEach(function(articolo) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = articolo.code;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.name;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.description;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.price;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				if (articolo.sold) {
					destcell.textContent = "venduto";
				} else {
					destcell.textContent = "non venduto";

				}
				row.appendChild(destcell);

				// Crea una cella con l'immagine
				linkcell = document.createElement("td");
				const img = document.createElement("img");
				img.src = "/Proj_tiw_RIA/resources/static/images/" + articolo.image;
				linkcell.appendChild(img);
				linkcell.classList.add("immagine"); // Aggiungi la classe "immagine" al <td>
				row.appendChild(linkcell);


				self.articoliAstaOffertaBody.appendChild(row);
			});
			rispostaJson.offerte.forEach(function(offerta) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = offerta.offerente;
				row.appendChild(destcell);
				destcell = document.createElement("td");
				destcell.textContent = offerta.offerta;
				row.appendChild(destcell);
				destcell = document.createElement("td");
				destcell.textContent = offerta.data;
				row.appendChild(destcell);

				self.offerteAstaOffertaBody.appendChild(row);
			});

			document.getElementById("astaidforofferta").setAttribute("value", rispostaJson.asta.idAsta);

			this.makeOfferTable.style.display = "block";
		}

		this.registerEvent = function(orchestrator) {
			this.makeOfferta.addEventListener("click", (e) => {
				if (e.target && e.target.matches("input[type='button']")) {
					var eventfieldset = e.target.closest("fieldset"),
						valid = true;
					for (i = 0; i < eventfieldset.elements.length; i++) {
						if (!eventfieldset.elements[i].checkValidity()) {
							eventfieldset.elements[i].reportValidity();
							valid = false;
							break;
						}
					}
					if (valid) {
						var self = this;
						console.log(e.target.closest("form"));
						var formData = new FormData(e.target.closest("form"));
						for (var pair of formData.entries()) {
							console.log(pair[0] + ', ' + pair[1]);
						}
						makeCall("POST", "CreateOfferta", e.target.closest("form"),
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									if (req.status == 200) {
										orchestrator.refresh(message);
									}
									if (req.status == 403) {
										window.location.href = req.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
									}
									else if (req.status != 200) {
										self.alert.textContent = message;
										self.reset();
									}
								}
							});
						this.makeOfferTable.style.display = "none";
						this.update("");
					}
				}
			});

		}

	}

	function ListAsteCookies(_listaAsteKeywordcookies, _alert, _listKeywordContainerBodycookies, _makeOfferTablecookies, _articoliAstaOffertacookies, _articoliAstaOffertaBodycookies, _offerteAstaOffertacookies, _offerteAstaOffertaBodycookies, _makeOffertacookies) {
		this.alert = _alert;
		this.listaAsteKeywordcookies = _listaAsteKeywordcookies;
		this.listKeywordContainerBodycookies = _listKeywordContainerBodycookies;
		this.makeOfferTablecookies = _makeOfferTablecookies;
		this.articoliAstaOffertacookies = _articoliAstaOffertacookies;
		this.articoliAstaOffertaBodycookies = _articoliAstaOffertaBodycookies;
		this.offerteAstaOffertacookies = _offerteAstaOffertacookies;
		this.offerteAstaOffertaBodycookies = _offerteAstaOffertaBodycookies;
		this.makeOffertacookies = _makeOffertacookies;

		this.reset = function() {
			this.listaAsteKeywordcookies.style.visibility = "hidden";
		}

		this.show = function() {
			var self = this;
			var rawCookies = getIdFromCookieSet(sessionStorage.getItem("username"));
			if (cookieExistence(sessionStorage.getItem("username"))) {
				makeCall("GET", "GetAsteAperteCookies?cookieAstaIdList=" + rawCookies, null,
					function(req) {
						if (req.readyState == 4) {
							var asteCookie = JSON.parse(req.responseText);
							if (req.status == 200) {
								self.update(asteCookie);
							}
						}

					});
			}
		}

		this.update = function(arrayAsteCookies) {
			var row, destcell, datecell, linkcell, anchor;
			this.listKeywordContainerBodycookies.innerHTML = "";
			var self = this;
			arrayAsteCookies.forEach(function(asta) {
				row = document.createElement("tr");

				destcell = document.createElement("td");
				destcell.textContent = "";
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.idAsta;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.scad;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.tempoMancante;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.prezzoIniziale;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = asta.rialzoMinimo;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				if (asta.offertaMax === -1) {
					destcell.textContent = "Ancora nessuna offerta";
				} else {
					destcell.textContent = asta.offertaMax;
				}
				row.appendChild(destcell);

				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Fai offerta");
				anchor.appendChild(linkText);
				anchor.setAttribute('idasta', asta.idAsta);
				anchor.addEventListener("click", () => {
					self.showOffertaForm(asta.idAsta);
					if (cookieExistence(sessionStorage.getItem("username"))) {
						var oldCookie = getCookieValue(sessionStorage.getItem("username"));
						updateOldCookie(sessionStorage.getItem("username"), oldCookie + asta.idAsta + ",");
					}
					else {
						createNewCookie(sessionStorage.getItem("username"), asta.idAsta + ",");
					}
					console.log(getCookieValue(sessionStorage.getItem("username")));
				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				self.listKeywordContainerBodycookies.appendChild(row);
			});
			this.listaAsteKeywordcookies.style.visibility = "visible";
		}

		this.showOffertaForm = function(_idAsta, next) {
			var self = this;
			this.idAsta = _idAsta;
			makeCall("GET", "GoToOfferta?idasta=" + this.idAsta, null,
				function(req) {
					if (req.readyState == 4) {
						var message = req.responseText;
						if (req.status == 200) {
							var rispostaJson = JSON.parse(req.responseText);
							if (rispostaJson.length == 0) {
								self.alert.textContent = "Offerte non disponibili";
								return;
							}
							self.updateOffertaForm(rispostaJson);
							if (next) next();
						} else if (req.status == 403) {
							window.location.href = req.getResponseHeader("Location");
							window.sessionStorage.removeItem('username');
						} else {
							self.alert.textContent = message;
						}
					}
				}
			)
		}

		this.updateOffertaForm = function(rispostaJson) {
			var row, destcell;
			var self = this;
			this.articoliAstaOffertaBodycookies.innerHTML = "";
			this.offerteAstaOffertaBodycookies.innerHTML = "";

			if (this.makeOfferTablecookies.style.display == "block") {
				this.makeOfferTablecookies.style.display = "none";
				return;
			}
			rispostaJson.articoli.forEach(function(articolo) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = articolo.code;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.name;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.description;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				destcell.textContent = articolo.price;
				row.appendChild(destcell);

				destcell = document.createElement("td");
				if (articolo.sold) {
					destcell.textContent = "venduto";
				} else {
					destcell.textContent = "non venduto";

				}
				row.appendChild(destcell);

				// Crea una cella con l'immagine
				linkcell = document.createElement("td");
				const img = document.createElement("img");
				img.src = "/Proj_tiw_RIA/resources/static/images/" + articolo.image;
				linkcell.appendChild(img);
				linkcell.classList.add("immagine"); // Aggiungi la classe "immagine" al <td>
				row.appendChild(linkcell);


				self.articoliAstaOffertaBodycookies.appendChild(row);
			});
			rispostaJson.offerte.forEach(function(offerta) {
				row = document.createElement("tr");
				destcell = document.createElement("td");
				destcell.textContent = offerta.offerente;
				row.appendChild(destcell);
				destcell = document.createElement("td");
				destcell.textContent = offerta.offerta;
				row.appendChild(destcell);
				destcell = document.createElement("td");
				destcell.textContent = offerta.data;
				row.appendChild(destcell);

				self.offerteAstaOffertaBodycookies.appendChild(row);
			});

			document.getElementById("astaidforoffertacookies").setAttribute("value", rispostaJson.asta.idAsta);

			this.makeOfferTablecookies.style.display = "block";
		}

		this.registerEvent = function(orchestrator) {
			this.makeOffertacookies.addEventListener("click", (e) => {
				if (e.target && e.target.matches("input[type='button']")) {
					var eventfieldset = e.target.closest("fieldset"),
						valid = true;
					for (i = 0; i < eventfieldset.elements.length; i++) {
						if (!eventfieldset.elements[i].checkValidity()) {
							eventfieldset.elements[i].reportValidity();
							valid = false;
							break;
						}
					}
					if (valid) {
						var self = this;
						console.log(e.target.closest("form"));
						var formData = new FormData(e.target.closest("form"));
						for (var pair of formData.entries()) {
							console.log(pair[0] + ', ' + pair[1]);
						}
						makeCall("POST", "CreateOfferta", e.target.closest("form"),
							function(req) {
								if (req.readyState == XMLHttpRequest.DONE) {
									var message = req.responseText;
									if (req.status == 200) {
										orchestrator.refresh(message);
									}
									if (req.status == 403) {
										window.location.href = req.getResponseHeader("Location");
										window.sessionStorage.removeItem('username');
									}
									else if (req.status != 200) {
										self.alert.textContent = message;
										self.reset();
									}
								}
							});
						this.makeOfferTablecookies.style.display = "none";
						this.update("");
					}
				}
			});

		}

	}

	function ManagerVendo(_alert, _divcontainer) {
		this.alertContainer = _alert;
		this.divContainer = _divcontainer;

		this.start = function() {
			// creazione pagina Vendo
			goToVendo = new GoToVendo(
				this.alertContainer,
				document.getElementById("id_openlistcontainer"),
				document.getElementById("id_openlistcontainerbody"),
				document.getElementById("id_closedlistcontainer"),
				document.getElementById("id_closedlistcontainerbody"),
				document.getElementById("id_articolicontainer"),
				document.getElementById("id_articolicontainerbody"),
				document.getElementById("id_astaform"),
				document.getElementById("id_detailcontainer"),
				document.getElementById("id_detailcontainerbody")
			);
			goToVendo.registerEvent1(this);
			goToVendo.registerEvent2(this);
			// creazione form per creare articoli
			articoliWizard = new ArticoliWizard(document.getElementById("id_articoloform"), this.alertContainer);
			articoliWizard.registerEvents(this);
		}

		this.refresh = function() {
			this.alertContainer.textContent = "";
			goToVendo.reset();
		}

		this.hide = function() {
			this.alertContainer.textContent = "";
			this.divContainer.style.display = "none";
		}

		this.show = function() {
			this.alertContainer.textContent = "";
			this.divContainer.style.display = "block";
		}
	}

	function ManagerAcquisto(_alert, _divcontainer) {
		this.alertContainer = _alert;
		this.divContainer = _divcontainer;

		this.start = function() {
			goToAcquisto = new GoToAcquisto(
				this.alertContainer,
				document.getElementById("id_asteaggiudicatecontainer"),
				document.getElementById("id_asteaggiudicatecontainerbody")
			);
			listaByKeyword = new ListAsteByKeyword(
				document.getElementById("id_listcontainerAsteRicercate"),
				this.alertContainer,
				document.getElementById("id_listcontainerbodyAsteRicercate"),
				document.getElementById("makeOfferTable"),
				document.getElementById("articoliAstaOfferta"),
				document.getElementById("articoliAstaOffertaBody"),
				document.getElementById("offerteAstaOfferta"),
				document.getElementById("offerteAstaOffertaBody"),
				document.getElementById("makeOfferta")
			)
			listAsteCookies = new ListAsteCookies(
				document.getElementById("id_listcontainerAsteRicercatecookies"),
				this.alertContainer,
				document.getElementById("id_listcontainerbodyAsteRicercatecookies"),
				document.getElementById("makeOfferTablecookies"),
				document.getElementById("articoliAstaOffertacookies"),
				document.getElementById("articoliAstaOffertaBodycookies"),
				document.getElementById("offerteAstaOffertacookies"),
				document.getElementById("offerteAstaOffertaBodycookies"),
				document.getElementById("makeOffertacookies")
			)
			listAsteCookies.registerEvent(this);
			listAsteCookies.show();
			listaByKeyword.registerEvent(this);
			document.getElementById("asteRicercateMacrotablecookies").style.display = "none";
			astaKeywordForm = new AstaByKeywordForm(document.getElementById("id_cercaform"), this.alertContainer);
			astaKeywordForm.registerEvent(this);
		}

		this.refresh = function() {
			this.alertContainer.textContent = "";
			goToAcquisto.reset();
		}

		this.hide = function() {
			this.alertContainer.textContent = "";
			this.divContainer.style.display = "none";
		}

		this.show = function() {
			this.alertContainer.textContent = "";
			this.divContainer.style.display = "block";
		}
	}

	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");
		var divVendo = document.getElementById("vendo");
		var divAcquisto = document.getElementById("acquisto");

		document.getElementById("id_gotoAcquisto").addEventListener("click", () => {
			vendo.hide();
			acquisto.show();
			document.getElementById("asteRicercateMacrotablecookies").style.display = "none";
		}, false);

		document.getElementById("id_gotoVendita").addEventListener("click", () => {
			acquisto.hide();
			vendo.show();
		}, false);

		document.querySelector("a[href='Logout']").addEventListener('click', () => {
			window.sessionStorage.removeItem('username');
		})

		this.start = function() {
			acquisto = new ManagerAcquisto(alertContainer, divAcquisto);
			vendo = new ManagerVendo(alertContainer, divVendo);
			acquisto.start();
			vendo.start();

		}

		this.showVendo = function() {
			vendo.show();
			acquisto.hide();
		}

		this.showAcquisto = function() {
			vendo.hide();
			acquisto.show();

		}

		this.refresh = function() {
			alertContainer.textContent = "";
			goToVendo.reset();
			goToAcquisto.reset();
			// all'inizio viene visualizzata la pagina vendo
			if (returnLastValueCookie(sessionStorage.getItem("username")) === "vendo") {
				this.showVendo();
			}
			else {
				this.showAcquisto();
				document.getElementById("asteRicercateMacrotablecookies").style.display = "block";
				document.getElementById("asteRicercateMacrotable").style.display = "none";
			}
			//this.showVendo();
			goToVendo.show();
			goToAcquisto.show();

		}
	}
}