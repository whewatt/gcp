#!/usr/bin/env python
import jinja2
import webapp2

env=jinja2.Environment(loader=jinja2.FileSystemLoader('templates'))
    
class MainHandler(webapp2.RequestHandler):
   def get(self):
       emails= [{'subject':'Lunch?', 'unread' : True},
            {'subject':'Google+ notification', 'unread' : False},
            {'subject':'Help! send me money from your account!', 'unread': True},
            {'subject':'Meeting on Thursday', 'unread' : False}]
       name = "Jane"
       #make sure you have the correct html file name here
       template = env.get_template('main.html')
       variables = {'name': name,
                    'emails':emails}
       self.response.write(template.render(variables))

app = webapp2.WSGIApplication([
    ('/', MainHandler)
], debug=True)
