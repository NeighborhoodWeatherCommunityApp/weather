// src/contexts/LevelUpContext.js
import React, {createContext, useContext, useState} from 'react';

const LevelUpContext = createContext();

export const LevelUpProvider = ({children}) => {
  const [levelUpInfo, setLevelUpInfo] = useState(null);

  const triggerLevelUp = levelTitle => {
    setLevelUpInfo({levelTitle});
  };

  const closeLevelUp = () => {
    setLevelUpInfo(null);
  };

  return (
    <LevelUpContext.Provider
      value={{levelUpInfo, triggerLevelUp, closeLevelUp}}>
      {children}
    </LevelUpContext.Provider>
  );
};

export const useLevelUp = () => useContext(LevelUpContext);
