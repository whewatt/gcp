import jinja2
import os
import logging
import webapp2 # webapp2 is a module that you import

jinja_environment = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))
    
class MainHandler(webapp2.RequestHandler): # This is the handler class
  def get(self):
    logging.info('>> Running new version')
    my_vars = {"name": "Mrs. Crabapple"}
    template = jinja_environment.get_template('templates/hello.html')
    self.response.out.write(template.render(my_vars))

class CountHandler(webapp2.RequestHandler):
  def get(self):
    for i in range(1, 101):
      self.response.write(i)

app = webapp2.WSGIApplication([
  ('/', MainHandler),
  ('/count', CountHandler),
], debug=True) # creates a WSGIApplication and assigns it to the variable app.
