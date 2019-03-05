var STORAGE = {
	SESSION : "session",
	LOCAL : "local"
};

function storeItem(key, value, storagetype) {
	try {
		switch (storagetype) {
			case STORAGE.LOCAL:
				localStorage.setItem(key, value);
				return true;
				break;
			case STORAGE.SESSION:
			default:
				sessionStorage.setItem(key, value);
				return true;
				break;
		}
	} catch (e) {
		if (e == QUOTA_EXCEEDED_ERROR) {
			debugLogs("Session Storage maximum size limit reached");
		}
	}
} // End of storeItem method

function deleteItem(key, storagetype) {
	switch (storagetype) {
		case STORAGE.LOCAL:
			localStorage.removeItem(key);
			return true;
			break;
		case STORAGE.SESSION:
		default:
			sessionStorage.removeItem(key);
			return true;
			break;
	}
} // End of deleteItem method

function fetchItemByKey(key, storagetype) {
	switch (storagetype) {
		case STORAGE.LOCAL:
			return localStorage.getItem(key);
			break;
		case STORAGE.SESSION:
		default:
			return sessionStorage.getItem(key);
			break;
	}
} // End of fetchItem method

function removeAllItems(storagetype) {
	switch (storagetype) {
		case STORAGE.LOCAL:
			localStorage.clear();
			break;
		case STORAGE.SESSION:
			sessionStorage.clear();
			break;
		default:
			localStorage.clear();
			sessionStorage.clear();
			break;
	}
} // End of removeAllItems method
