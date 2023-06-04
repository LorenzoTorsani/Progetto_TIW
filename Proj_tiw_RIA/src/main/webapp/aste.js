{
	// page components
	let goToVendo, goToAcquisto, asteWizard, pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();

		} // display initial content
	}, false);

	function GoToVendo(_alert, _openlistcontainer, _openlistcontainerbody, _closedlistcontainer, _closedlistcontainerbody, _articolicontainer, _articolicontainerbody, _astewizard) {
		this.alert = _alert;
		this.openlistcontainer = _openlistcontainer;
		this.openlistcontainerbody = _openlistcontainerbody;
		this.closedlistcontainer = _closedlistcontainer;
		this.closedlistcontainerbody = _closedlistcontainerbody;
		this.articolicontainer = _articolicontainer;
		this.articolicontainerbody = _articolicontainerbody;
		this.astewizard = _astewizard;

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
				linkText = document.createTextNode("Close");
				anchor.appendChild(linkText);
				// make list item clickable
				anchor.setAttribute('asta_id', asta.idAsta);
				anchor.addEventListener("click", (e) => {
					// quando clicco chiudo e aggiorno
					listaAsteChiuse.reset();
					listaAsteAperte.reset();
					listaAsteAperte.close(e.target.getAttribute("asta_id"));

				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);

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


		this.updateArticoli = function(arrayArticoli) {
			var row, destcell, datecell, linkcell, anchor, linkText;
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
				destcell.textContent = articolo.sold;
				row.appendChild(destcell);

				// Crea una cella con un link
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
				var checkbox = document.createElement("input");
				checkbox.setAttribute("type", "checkbox");
				checkbox.setAttribute("name", "articoli");
				checkbox.setAttribute("id", articolo.code);
				checkbox.setAttribute("value", articolo.code);

				var label = document.createElement("label");
				label.textContent = articolo.name;

				fieldset.appendChild(checkbox);
				fieldset.appendChild(label);
				fieldset.appendChild(document.createElement("br"));

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

					}
				}

			});
		}

	}

	/*function AsteWizard(wizardId, alert, arrayArticoli) {
		this.wizard = wizardId;
		this.alert = alert;
		arrayArticoli.forEach(function(articolo){
			this.wizard.querySelector('input[type="checkbox"].articolo.name').setAttribute("text", articolo.name);
			
		});
	}*/

	function GoToAcquisto(_alertContainer, _aggiudicatelistcontainer, _aggiudicatelistcontainerbody) {
		this.alert = _alertContainer,
			this.aggiudicatelistcontainer = _aggiudicatelistcontainer,
			this.aggiudicatelistcontainerbody = _aggiudicatelistcontainerbody,

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
							console.log(asteToShow);
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

		this.update = function(arrayAste) {
			var row, destcell;
			this.aggiudicatelistcontainerbody.innerHTML = "";

			var self = this;
			arrayAste.forEach(function(asta) {
				console.log(asta);
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

	function AstaByKeywordForm(astabykeywordformId, alertContainer) {
		this.astabykeywordformId = astabykeywordformId;
		this.alert = alertContainer;
		this.keyword = null;

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

	function ListAsteByKeyword(listaAsteKeyword, alert, listKeywordContainerBody) {
		this.alert = alert;
		this.listaAsteKeyword = listKeywordContainerBody;
		this.listKeywordContainerBody = listKeywordContainerBody;

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
				destcell.textContent = asta.offertaMax;
				row.appendChild(destcell);
				
				
				self.listKeywordContainerBody.appendChild(row);
			});
			this.listaAsteKeyword.style.visibility = "visible";
		}
	}

	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			//personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
			//	document.getElementById("id_username"));
			//personalMessage.show();

			goToVendo = new GoToVendo(
				alertContainer,
				document.getElementById("id_openlistcontainer"),
				document.getElementById("id_openlistcontainerbody"),
				document.getElementById("id_closedlistcontainer"),
				document.getElementById("id_closedlistcontainerbody"),
				document.getElementById("id_articolicontainer"),
				document.getElementById("id_articolicontainerbody"),
				document.getElementById("id_astaform")
			);
			goToVendo.registerEvent1(this);
			//asteWizard = new AsteWizard(document.getElementById("id_astewizard"), alertContainer, arrayArticoli);
			//asteWizard.registerEvent(this);

			goToAcquisto = new GoToAcquisto(
				alertContainer,
				document.getElementById("id_asteaggiudicatecontainer"),
				document.getElementById("id_asteaggiudicatecontainerbody")
			);

			articoliWizard = new ArticoliWizard(document.getElementById("id_articoloform"), alertContainer);
			articoliWizard.registerEvents(this);

			listaByKeyword = new ListAsteByKeyword(
				document.getElementById("id_listcontainerAsteRicercate"),
				alertContainer,
				document.getElementById("id_listcontainerbodyAsteRicercate")
			)

			astaKeywordForm = new AstaByKeywordForm(document.getElementById("id_cercaform"), alertContainer);
			astaKeywordForm.registerEvent(this);

			document.querySelector("a[href='Logout']").addEventListener('click', () => {
				window.sessionStorage.removeItem('username');
			})

			/*			asteChiuseList = new AsteChiuseList(
							alertContainer,
							document.getElementById("id_closedlistcontainer"),
							document.getElementById("id_openlistcontainer"));
							*/
		};

		this.refresh = function() {
			goToVendo.show();
			goToAcquisto.show();
			articoliWizard.reset();
		};
	}
};