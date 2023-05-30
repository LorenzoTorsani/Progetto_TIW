{
	// page components
	let goToVendo, pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();

		} // display initial content
	}, false);

	function GoToVendo(_alert, _openlistcontainer, _openlistcontainerbody, _closedlistcontainer, _closedlistcontainerbody, _articolicontainer, _articolicontainerbody) {
		this.alert = _alert;
		this.openlistcontainer = _openlistcontainer;
		this.openlistcontainerbody = _openlistcontainerbody;
		this.closedlistcontainer = _closedlistcontainer;
		this.closedlistcontainerbody = _closedlistcontainerbody;
		this.articolicontainer = _articolicontainer;
		this.articolicontainerbody = _articolicontainerbody;

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
							self.updateAsteAperte(asteToShow.asteAperte, asteToShow.articoli);
							self.updateAsteChiuse(asteToShow.asteChiuse);
							self.updateArticoli(asteToShow.articoli);
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
				anchor = document.createElement("a");
				anchor.href = articolo.immagineUrl; // Assumendo che asta.immagineUrl sia l'URL dell'immagine
				anchor.target = "_blank"; // Apre il link in una nuova scheda
				linkText = document.createTextNode("Visualizza immagine"); // Testo del link
				anchor.appendChild(linkText);
				linkcell.appendChild(anchor);
				row.appendChild(linkcell);

				self.articolicontainerbody.appendChild(row);
			});

			this.articolicontainer.style.visibility = "visible";
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
				document.getElementById("id_articolicontainerbody")
			);

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