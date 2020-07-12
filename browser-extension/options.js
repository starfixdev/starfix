
// Saves options to chrome.storage
function save_options() {
    var method = document.getElementById('method').value;
    chrome.storage.sync.set({
      method: method
    }, function() {
      //Reload the clone link with specified protocol
      chrome.tabs.query({active: true, currentWindow: true}, function (arrayOfTabs) {
        chrome.tabs.reload(arrayOfTabs[0].id);
      });
      // Update status to let user know options were saved.
      var status = document.getElementById('status');
      status.textContent = 'Configuration saved !!';
      setTimeout(function() {
        status.textContent = '';
      }, 1750);
    });
  }
  
  // Restores select box and checkbox state using the preferences
  // stored in chrome.storage.
  function restore_options() {
    // Use default value mthod= 'https' 
    chrome.storage.sync.get({
      method: 'https'
    }, function(items) {
      document.getElementById('method').value = items.method;
    });
  }
  document.addEventListener('DOMContentLoaded', restore_options);
  document.getElementById('save').addEventListener('click',
      save_options);