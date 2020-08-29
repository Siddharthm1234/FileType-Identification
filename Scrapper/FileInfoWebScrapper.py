"""
#Web scraper for fileinfo.com
#Arguments :  None
#Returns   :  .csv and .json file with following attributes               
#          1. Extension
#          2. Name
#          3. Category
#          4. Application
#          5. Description
"""


from bs4 import BeautifulSoup
import requests
import pandas as pd
import csv
import json 


def getData(soup, cat):
    """
    #Scrapes data from website
    #Arguments:
    #Argument1 (BeautifulSoup): Soup object holding the website data
    #Argument2 (String)       : String value holding category of file extension
    
    #Returns (DataFrame): DataFrame containing 5 columns (Extension, Name, Category, Application, Description)
    """
    extension = []
    filename = []
    category=[]
    application=[]
    description=[]
    
    table = soup.find('tbody')
    for tr in table.findAll('tr'):
        i=0
        for td in tr.findAll('td'):
            if i==0:
                extension.append(td.text)
            if i==1:
                filename.append(td.text)
            if i==2:
                category.append(cat)
            i+=1


    def getAdditionalData(ext):
        """
        #Scrapes additional data for each extension
        #Arguments:
        #Argument1 (String): String value of file extension
        
        #Returns: None 
        """
        ext = ext.lower()[1:] 
        subapp=[]
        url = 'https://fileinfo.com/extension/' + ext
        print(url)
        response = requests.get(url)
        soup = BeautifulSoup(response.content, "html.parser")
        
        for app in soup.findAll('table', attrs={"class":"apps"}):
            osList = list((app.text).split('\n'))
            for appSubSet in osList:
                if appSubSet and appSubSet not in subapp:
                    subapp.append(appSubSet)
        
        allapps = ", ".join(subapp)
        application.append(allapps)
        
        desc = soup.find('div', class_='infoBox')
        dscr = desc.text.lstrip().rstrip()
        description.append(dscr)
    
    
   
    for ext in extension:
        getAdditionalData(ext)
        
    df = pd.DataFrame({"Extension":extension, "Name":filename, "Category": category, "Applications": application, "Description":description})
    return df

url = "https://fileinfo.com/filetypes/"

#Creating DataFrame of all Text Files
response = requests.get(url+"text")
soup = BeautifulSoup(response.content, "html.parser")
dfText = getData(soup, "Text File")

#Creating DataFrame of all Developer Files
response = requests.get(url+"developer")
soup = BeautifulSoup(response.content, "html.parser")
dfDeveloper = getData(soup, "Developer File")

#Creating DataFrame of all Database Files
response = requests.get(url+"database")
soup = BeautifulSoup(response.content, "html.parser")
dfDatabase = getData(soup, "Database File")

#Creating DataFrame of all Executable Files
response = requests.get(url+"executable")
soup = BeautifulSoup(response.content, "html.parser")
dfExecutable = getData(soup, "Executable File")

#Creating DataFrame of all Config Files
response = requests.get(url+"settings")
soup = BeautifulSoup(response.content, "html.parser")
dfConfig = getData(soup, "Config File")

#Creating DataFrame of all Data Files
response = requests.get(url+"data")
soup = BeautifulSoup(response.content, "html.parser")
dfData = getData(soup, "Data File")

#Creating DataFrame of all Raster Image Files
response = requests.get(url+"raster_image")
soup = BeautifulSoup(response.content, "html.parser")
dfRasterImage = getData(soup, "Raster Image File")

#Concatinating all DataFrame into a Single DataFrame and storing it in ExtensionFileInfo.csv
dfAllFiles = pd.concat([dfText, dfDeveloper, dfDatabase, dfExecutable, dfConfig, dfData, dfRasterImage], ignore_index=True)
dfAllFiles.to_csv("ExtensionFileInfo.csv" , index=False)

#Reading the ExtensionFileInfo.csv file created and writing it into ExtensionFileInfo.json
with open("source1.csv", "r") as f:
    reader = csv.reader(f)
    next(reader)  #skip the heading
    data =[]
    for row in reader:
        data.append({"Extension":row[0], 
                     "Name": row[1],
                     "Category":row[2],
                     "Application":row[3],
                     "Description": row[4]})
    
with open("source1.json", "w") as f:
    json.dump(data, f, indent=4)







