// ======================================================================
// \title  HealthTopologyAc.hpp
// \author Generated by fpp-to-cpp
// \brief  hpp file for Health topology
// ======================================================================

#ifndef M_HealthTopologyAc_HPP
#define M_HealthTopologyAc_HPP

#include "C.hpp"
#include "Health.hpp"
#include "HealthTopologyDefs.hpp"

// ----------------------------------------------------------------------
// Component instances
// ----------------------------------------------------------------------

namespace M {

  //! c1
  extern M::C c1;

}

namespace M {

  //! c2
  extern M::C c2;

}

namespace M {

  //! health
  extern Svc::Health health;

}

namespace M {

  // ----------------------------------------------------------------------
  // Constants
  // ----------------------------------------------------------------------

  namespace BaseIds {
    enum {
      M_health = 0x100,
      M_c1 = 0x200,
      M_c2 = 0x300,
    };
  }

  namespace InstanceIds {
    enum {
      M_c1,
      M_c2,
      M_health,
    };
  }

  // ----------------------------------------------------------------------
  // Component configuration objects
  // ----------------------------------------------------------------------

  namespace ConfigObjects {

    namespace M_health {
      //!< Number of entries in the pingEntryies array
      constexpr FwSizeType NUM_PING_ENTRIES = 2;
      //!< Ping entry configuration for Svc::Health
      extern Svc::Health::PingEntry pingEntries[NUM_PING_ENTRIES];
    }

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
