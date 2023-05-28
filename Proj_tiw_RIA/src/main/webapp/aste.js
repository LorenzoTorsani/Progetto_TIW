{
	// page components
	let asteAperteList, asteChiuselist, pageOrchestrator = new PageOrchestrator(); // main controller

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh();

		} // display initial content
	}, false);

	function PersonalMessage(_username, messagecontainer) {
		this.username = _username;
		this.show = function() {
			messagecontainer.textContent = this.username;
		}
	}

	function AsteAperteList(_alert, _listcontainer, _listcontainerbody) {
		this.alert = _alert;
		this.listcontainer = _listcontainer;
		this.listcontainerbody = _listcontainerbody;

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
							//		console.log(JSON.parse(req.responseText));
							if (asteToShow.length == 0) {
								self.alert.textContent = "Nessuna asta aperta";
								return;
							}
							/*if (asteToShow.asteChiuse.length == 0) {
								self.alert.textContent = "Nessuna asta chiusa";
								return;
							}
							*/
							self.updateAsteAperte(asteToShow);
							console.log(asteToShow.idAsta);
							//self.updateAsteChiuse(asteToShow.astechiuse);
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

		this.updateAsteAperte = function(arrayAste) {
			var elem, i, row, destcell, datecell, linkcell, anchor;
			this.listcontainerbody.innerHTML = ""; // empty the table body
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

				anchor.setAttribute('asta_id', asta.asta_id); // set a custom HTML attribute
				anchor.addEventListener("click", (e) => {
					astaApertaDetails.show(e.target.getAttribute("asta_id")); // the list must know the details container
					listaOfferteAsteAperte.reset();
					listaOfferteAsteAperte.show(e.target.getAttribute("asta_id")); // the list must know the details container
				}, false);
				anchor.href = "#";
				self.listcontainerbody.appendChild(row);
				linkcell = document.createElement("td");
				anchor = document.createElement("a");
				linkcell.appendChild(anchor);
				linkText = document.createTextNode("Close");
				anchor.appendChild(linkText);
				// make list item clickable
				anchor.setAttribute('asta_id', asta.asta_id);
				anchor.addEventListener("click", (e) => {
					// quando clicco chiudo e aggiorno
					listaAsteChiuse.reset();
					listaAsteAperte.reset();
					listaAsteAperte.close(e.target.getAttribute("asta_id"));

				}, false);
				anchor.href = "#";
				row.appendChild(linkcell);
				row.appendChild(linkcell);
				self.listcontainerbody.appendChild(row);
			});
			this.listcontainer.style.visibility = "visible";

		}

/*
		this.updateAsteChiuse = function(arrayAste) {
			var elem, i, row, destcell, datecell, linkcell, anchor;
			this.listcontainerbody.innerHTML = "";
			var self = this;

		}
		
		*/
	}

	/*
		function AsteChiuseList(_alert, _listcontainer, _listcontainerbody) {
			this.alert = _alert;
			this.listcontainer = _listcontainer;
			this.listcontainerbody = _listcontainerbody;
			
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
								if (asteToShow.length == 0) {
									self.alert.textContent = "Nessuna asta";
									return;
								}
								self.update(asteToShow);
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
		}
		*/

	function PageOrchestrator() {
		var alertContainer = document.getElementById("id_alert");

		this.start = function() {
			//personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
			//	document.getElementById("id_username"));
			//personalMessage.show();

			asteAperteList = new AsteAperteList(
				alertContainer,
				document.getElementById("id_openlistcontainer"),
				document.getElementById("id_openlistcontainerbody"));
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
			asteAperteList.show();
			//			asteChiuseList.show();
		}
	}
};