{
	// page components
	let goToVendo, asteWizard, pageOrchestrator = new PageOrchestrator(); // main controller

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
			this.listcontainer.style.visibility = "hidden";
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
				linkcell.appendChild(document.createElement("img")).src = "/Proj_tiw_RIA/resources/static/images/" + articolo.image;
				//anchor = document.createElement("a");
				//var folderPath = "/Users/simonezacchetti/Desktop/immagini/";
				//var immagineUrl = folderPath + articolo.image;
				//anchor.href = immagineUrl; // Assumendo che asta.immagineUrl sia l'URL dell'immagine
				//anchor.target = "_blank"; // Apre il link in una nuova scheda
				//linkText = document.createTextNode("Visualizza immagine"); // Testo del link
				//anchor.appendChild(linkText);
				//linkcell.appendChild(anchor);
				row.appendChild(linkcell);

				self.articolicontainerbody.appendChild(row);
			});

			this.articolicontainer.style.visibility = "visible";
		}

		this.updateAsteWizard = function(arrayArticoli) {
			var self = this;
			this.astewizard.innerHTML = "";
			arrayArticoli.forEach(function(articolo) {
				var checkbox = document.createElement("input");
				checkbox.setAttribute("type", "checkbox");
				checkbox.setAttribute("name", articolo.name);

				var label = document.createElement("label");
				label.textContent = articolo.name;

				var br = document.createElement("br");

				self.astewizard.appendChild(checkbox);
				self.astewizard.appendChild(label);
				self.astewizard.appendChild(br);
			});

			this.astewizard.style.visibility = "visible";
		}

		this.registerEvent1 = function(orchestrator){
			this.astewizard.querySelector("input[type='button'].submit").addEventListener('click', (e)=> {
				var eventfieldset = e.target.closest("fieldset"),
				valid = true;
				for(i = 0; i < eventfieldset.elements.length; i++){
					if(!eventfieldset.elements[i].checkValidity()){
						eventfieldset.elements[i].reportValidity();
						valid = false;
						break;
					}
				}
				if(valid){
					var self = this;
					makeCall("POST", 'CreateAsta', e.target.closest("form"),
					function(req){
						if(req.readyState == XMLHttpRequest.DONE){
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
				document.getElementById("id_astewizard")
			);

			//asteWizard = new AsteWizard(document.getElementById("id_astewizard"), alertContainer, arrayArticoli);
			//asteWizard.registerEvent(this);

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
		}
	}
};