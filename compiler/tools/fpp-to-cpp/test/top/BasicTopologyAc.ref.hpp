// ======================================================================
// \title  BasicTopologyAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Basic topology
// ======================================================================

#ifndef M_BasicTopologyAc_HPP
#define M_BasicTopologyAc_HPP

#include "Active.hpp"
#include "BasicTopologyDefs.hpp"
#include "Passive.hpp"

// ----------------------------------------------------------------------
// Component instances
// ----------------------------------------------------------------------

namespace M {

  //! active2
  extern M::Active active2;

}

namespace M {

  //! active3
  extern M::Active active3;

}

namespace M {

  //! passive1
  extern M::Passive passive1;

}

namespace M {

  //! passive2
  extern ConcretePassive passive2;

}

//! active1
extern M::Active active1;

namespace M {

  // ----------------------------------------------------------------------
  // Constants
  // ----------------------------------------------------------------------

  namespace ConfigConstants {
    namespace M_active2 {
      enum {
        X = 0,
        Y = 1
      };
    }
  }

  namespace BaseIds {
    enum {
      active1 = 0x100,
      M_active2 = 0x200,
      M_active3 = 0x300,
      M_passive1 = 0x300,
      M_passive2 = 0x400,
    };
  }

  namespace CPUs {
    enum {
      active1 = 0,
    };
  }

  namespace InstanceIds {
    enum {
      M_active2,
      M_active3,
      M_passive1,
      M_passive2,
      active1,
    };
  }

  namespace Priorities {
    enum {
      active1 = 1,
    };
  }

  namespace QueueSizes {
    enum {
      M_active2 = 10,
      M_active3 = 10,
      active1 = 10,
    };
  }

  namespace StackSizes {
    enum {
      active1 = 1024,
    };
  }

  namespace TaskIds {
    enum {
      M_active2,
      M_active3,
      active1,
    };
  }

  // ----------------------------------------------------------------------
  // Helper functions
  // ----------------------------------------------------------------------

  //! Initialize components
  void initComponents(
      const TopologyState& state //!< The topology state
  );

  //! Configure components
  void configComponents(
      const TopologyState& state //!< The topology state
  );

  //! Set component base Ids
  void setBaseIds();

  //! Connect components
  void connectComponents();

  //! Register commands
  void regCommands();

  //! Read parameters
  void readParameters();

  //! Load parameters
  void loadParameters();

  //! Start tasks
  void startTasks(
      const TopologyState& state //!< The topology state
  );

  //! Stop tasks
  void stopTasks(
      const TopologyState& state //!< The topology state
  );

  //! Free threads
  void freeThreads(
      const TopologyState& state //!< The topology state
  );

  //! Tear down components
  void tearDownComponents(
      const TopologyState& state //!< The topology state
  );

  // ----------------------------------------------------------------------
  // Setup and teardown functions
  // ----------------------------------------------------------------------

  //! Set up the topology
  void setup(
      const TopologyState& state //!< The topology state
  );

  //! Tear down the topology
  void teardown(
      const TopologyState& state //!< The topology state
  );

}

#endif
